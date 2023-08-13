package cn.soboys.simplest.jpa.interceptor;

import cn.soboys.simplest.jpa.config.TenantProperties;
import cn.soboys.simplest.jpa.plugin.CustomTenant;
import cn.soboys.simplest.jpa.plugin.TenantFactory;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.extra.spring.SpringUtil;
import org.hibernate.resource.jdbc.spi.StatementInspector;

import java.util.List;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/30 12:54
 * @webSite https://github.com/coder-amiao
 */
@Slf4j
@Data
@Accessors(chain = true)
public class TenantInterceptor implements StatementInspector {


    /**
     * 当前租户ID，从UserContext获取
     */
    private String tenantId;

    /**
     * 需进行租户解析的表名，需要注入
     */
    private List<String> tenantTables;

    /**
     * 需进行租户解析的租户字段名,本项目中为固定名称
     */
    private String tenantIdColumn = "tenant_id";

    /**
     * 重写StatementInspector的inspect接口，参数为hibernate处理后的原始SQL，返回值为我们修改后的SQL
     *
     * @param sql
     * @return
     */
    @Override
    public String inspect(String sql) {
        /**
         * 从当前线程获取登录用户的所属租户ID
         */
        TenantProperties tenantProperties = SpringUtil.getBean(TenantProperties.class);

        TenantFactory tenantFactory = SpringUtil.getBean(TenantFactory.class);

        if (tenantProperties.getEnableTenant() &&
                !StrUtil.isEmptyIfStr(tenantFactory.getTenantId()) && tenantProperties.getTables().size() > 0) {

            //自定义忽略某个sql 不进行租户拦截
            if (CustomTenant.withoutTenantCondition) {
                CustomTenant.withoutTenantCondition = false;
                return sql;
            }
            //当前租户ID
            tenantId = tenantFactory.getTenantId();
            if (StrUtil.isEmpty(tenantId)) return sql;
            // 初始化需要进行租户解析的租户表
            tenantIdColumn = tenantProperties.getTenantIdColumn();
            tenantTables = tenantProperties.getTables();
            try {
                /**
                 *   不进行租户条件解析
                 */
//            if (UserContext.current() == null || UserContext.current().getAdministrator()) {
//                return null;
//            }
                log.debug("租户解析开始，原始SQL：{}", sql);
                Statements statements = CCJSqlParserUtil.parseStatements(sql);
                StringBuilder sqlStringBuilder = new StringBuilder();
                int i = 0;
                for (Statement statement : statements.getStatements()) {
                    if (null != statement) {
                        if (i++ > 0) {
                            sqlStringBuilder.append(';');
                        }
                        sqlStringBuilder.append(this.processParser(statement));
                    }
                }
                String newSql = sqlStringBuilder.toString();
                log.debug("租户解析结束，解析后SQL：{}", newSql);
                return newSql;
            } catch (Exception e) {
                log.error("租户解析失败，解析SQL异常{}", e.getMessage());
                e.printStackTrace();
            } finally {
                tenantId = null;
            }
        }
        return sql;
    }

    private String processParser(Statement statement) {
        if (statement instanceof Insert) {
            this.processInsert((Insert) statement);
        } else if (statement instanceof Select) {
            this.processSelectBody(((Select) statement).getSelectBody());
        } else if (statement instanceof Update) {
            this.processUpdate((Update) statement);
        } else if (statement instanceof Delete) {
            this.processDelete((Delete) statement);
        }
        /**
         * 返回处理后的SQL
         */
        return statement.toString();
    }

    /**
     * select 语句处理
     */

    public void processSelectBody(SelectBody selectBody) {
        if (selectBody instanceof PlainSelect) {
            processPlainSelect((PlainSelect) selectBody);
        } else if (selectBody instanceof WithItem) {
            WithItem withItem = (WithItem) selectBody;
            if (withItem.getSelectBody() != null) {
                processSelectBody(withItem.getSelectBody());
            }
        } else {
            SetOperationList operationList = (SetOperationList) selectBody;
            if (operationList.getSelects() != null && operationList.getSelects().size() > 0) {
                operationList.getSelects().forEach(this::processSelectBody);
            }
        }
    }

    /**
     * insert 语句处理
     */

    public void processInsert(Insert insert) {
        if (tenantTables.contains(insert.getTable().getFullyQualifiedName())) {

            //如果存在租户列覆盖实体类租户列
            List<Column> Columns = insert.getColumns();
            Integer existColumIndex = null;
            for (int i = 0; i < Columns.size(); i++) {
                if (Columns.get(i).getColumnName().equals(tenantIdColumn)) {
                    existColumIndex = i;
                    break;
                }
            }
            if (existColumIndex == null) insert.getColumns().add(new Column(tenantIdColumn));
            if (insert.getSelect() != null) {
                processPlainSelect((PlainSelect) insert.getSelect().getSelectBody(), true);
            } else if (insert.getItemsList() != null) {

                ItemsList itemsList = insert.getItemsList();
                if (existColumIndex == null)
                    ((ExpressionList) insert.getItemsList()).getExpressions().add(new StringValue(tenantId));

                //替换指定列租户值
//                ((ExpressionList) itemsList).getExpressions().remove(existColumIndex.intValue());
//                ((ExpressionList) itemsList).getExpressions().add(existColumIndex,new StringValue(tenantId));
//                insert.setItemsList(itemsList);


            } else {
                throw new RuntimeException("Failed to process multiple-table update, please exclude the tableName or statementId");
            }
        }
    }

    /**
     * update 语句处理
     */

    public void processUpdate(Update update) {
        final Table table = update.getTable();
        if (tenantTables.contains(table.getFullyQualifiedName())) {
            update.setWhere(this.andExpression(table, update.getWhere()));
        }
    }

    /**
     * delete 语句处理
     */

    public void processDelete(Delete delete) {
        if (tenantTables.contains(delete.getTable().getFullyQualifiedName())) {
            delete.setWhere(this.andExpression(delete.getTable(), delete.getWhere()));
        }
    }

    /**
     * delete update 语句 where 处理
     */
    protected BinaryExpression andExpression(Table table, Expression where) {
        //获得where条件表达式
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(this.getAliasColumn(table));
        equalsTo.setRightExpression(new StringValue(tenantId));
        if (null != where) {
            if (where instanceof OrExpression) {
                return new AndExpression(equalsTo, new Parenthesis(where));
            } else {
                return new AndExpression(equalsTo, where);
            }
        }
        return equalsTo;
    }

    /**
     * 处理 PlainSelect
     */
    protected void processPlainSelect(PlainSelect plainSelect) {
        processPlainSelect(plainSelect, false);
    }

    /**
     * 处理 PlainSelect
     *
     * @param plainSelect ignore
     * @param addColumn   是否添加租户列,insert into select语句中需要
     */
    protected void processPlainSelect(PlainSelect plainSelect, boolean addColumn) {
        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table) {
            Table fromTable = (Table) fromItem;
            if (tenantTables.contains(fromTable.getFullyQualifiedName())) {
                //#1186 github
                plainSelect.setWhere(builderExpression(plainSelect.getWhere(), fromTable));
                if (addColumn) {
                    plainSelect.getSelectItems().add(new SelectExpressionItem(
                            new Column(tenantIdColumn)));
                }
            }
        } else {
            processFromItem(fromItem);
        }
        List<Join> joins = plainSelect.getJoins();
        if (joins != null && joins.size() > 0) {
            joins.forEach(j -> {
                processJoin(j);
                processFromItem(j.getRightItem());
            });
        }
    }

    /**
     * 处理子查询等
     */
    protected void processFromItem(FromItem fromItem) {
        if (fromItem instanceof SubJoin) {
            SubJoin subJoin = (SubJoin) fromItem;
            if (subJoin.getJoinList() != null) {
                subJoin.getJoinList().forEach(this::processJoin);
            }
            if (subJoin.getLeft() != null) {
                processFromItem(subJoin.getLeft());
            }
        } else if (fromItem instanceof SubSelect) {
            SubSelect subSelect = (SubSelect) fromItem;
            if (subSelect.getSelectBody() != null) {
                processSelectBody(subSelect.getSelectBody());
            }
        } else if (fromItem instanceof ValuesList) {
            log.debug("Perform a subquery, if you do not give us feedback");
        } else if (fromItem instanceof LateralSubSelect) {
            LateralSubSelect lateralSubSelect = (LateralSubSelect) fromItem;
            if (lateralSubSelect.getSubSelect() != null) {
                SubSelect subSelect = lateralSubSelect.getSubSelect();
                if (subSelect.getSelectBody() != null) {
                    processSelectBody(subSelect.getSelectBody());
                }
            }
        }
    }

    /**
     * 处理联接语句
     */
    protected void processJoin(Join join) {
        if (join.getRightItem() instanceof Table) {
            Table fromTable = (Table) join.getRightItem();
            if (tenantTables.contains(fromTable.getFullyQualifiedName())) {
                join.setOnExpression(builderExpression(join.getOnExpression(), fromTable));
            }
        }
    }

    /**
     * 处理条件:
     * 支持 getTenantHandler().getTenantId()是一个完整的表达式：tenant in (1,2)
     * 默认tenantId的表达式： LongValue(1)这种依旧支持
     */
    protected Expression builderExpression(Expression currentExpression, Table table) {
        final Expression tenantExpression = new StringValue(tenantId);
        Expression appendExpression;
        if (!(tenantExpression instanceof SupportsOldOracleJoinSyntax)) {
            appendExpression = new EqualsTo();
            ((EqualsTo) appendExpression).setLeftExpression(this.getAliasColumn(table));
            ((EqualsTo) appendExpression).setRightExpression(tenantExpression);
        } else {
            appendExpression = processTableAlias4CustomizedTenantIdExpression(tenantExpression, table);
        }
        if (currentExpression == null) {
            return appendExpression;
        }
        if (currentExpression instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) currentExpression;
            doExpression(binaryExpression.getLeftExpression());
            doExpression(binaryExpression.getRightExpression());
        } else if (currentExpression instanceof InExpression) {
            InExpression inExp = (InExpression) currentExpression;
            ItemsList rightItems = inExp.getRightItemsList();
            if (rightItems instanceof SubSelect) {
                processSelectBody(((SubSelect) rightItems).getSelectBody());
            }
        }
        if (currentExpression instanceof OrExpression) {
            return new AndExpression(new Parenthesis(currentExpression), appendExpression);
        } else {
            return new AndExpression(currentExpression, appendExpression);
        }
    }

    protected void doExpression(Expression expression) {
        if (expression instanceof FromItem) {
            processFromItem((FromItem) expression);
        } else if (expression instanceof InExpression) {
            InExpression inExp = (InExpression) expression;
            ItemsList rightItems = inExp.getRightItemsList();
            if (rightItems instanceof SubSelect) {
                processSelectBody(((SubSelect) rightItems).getSelectBody());
            }
        }
    }

    /**
     * 目前: 针对自定义的tenantId的条件表达式[tenant_id in (1,2,3)]，无法处理多租户的字段加上表别名
     * select a.id, b.name
     * from a
     * join b on b.aid = a.id and [b.]tenant_id in (1,2) --别名[b.]无法加上 TODO
     *
     * @param expression
     * @param table
     * @return 加上别名的多租户字段表达式
     */
    protected Expression processTableAlias4CustomizedTenantIdExpression(Expression expression, Table table) {
        //cannot add table alias for customized tenantId expression,
        // when tables including tenantId at the join table poistion
        return expression;
    }

    /**
     * 租户字段别名设置
     * <p>tableName.tenantId 或 tableAlias.tenantId</p>
     *
     * @param table 表对象
     * @return 字段
     */
    protected Column getAliasColumn(Table table) {
        StringBuilder column = new StringBuilder();
        if (null == table.getAlias()) {
            column.append(table.getName());
        } else {
            column.append(table.getAlias().getName());
        }
        column.append(".");
        column.append(tenantIdColumn);
        return new Column(column.toString());
    }

}
