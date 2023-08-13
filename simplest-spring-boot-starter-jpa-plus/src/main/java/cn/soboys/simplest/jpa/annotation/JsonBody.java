package cn.soboys.simplest.jpa.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version: 2.2
 * @author:  syl
 * @description: Json注解，用于解析对象为Json
 * @date: 2019/11/25
*/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonBody {

	//定位表达式解析根
	String root() default "result[]";

	//属性串表达式
	String expression() default "";

	//maxLevel 仅针对root外的对象和Strategy.DEFAULT_SHOW模式下root下的对象
	int maxLevel() default 5;

	//是否调用对象的toJsonObject接口，为true时，针对该对象的属性指定将不生效
	boolean useCustom() default false;

	//忽略懒加载，使用了懒加载且未初始化的值将被置为NULL
	boolean ignoreLazy() default false;

	//策略模式
	Strategy strategy() default Strategy.DEFAULT_NONE;

	public static enum Strategy {
		//如果不指定，属性默认不显示
		DEFAULT_NONE,
		//如果不指定，属性默认显示
		DEFAULT_SHOW;

		private Strategy() {
		}
	}
}
