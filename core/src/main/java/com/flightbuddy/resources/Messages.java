package com.flightbuddy.resources;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.context.annotation.ApplicationScope;

@ApplicationScope
@Slf4j
public class Messages{
	private static final String MESSAGE_BUNDLE_NAME = "classpath:bundle/messages";
	private static final ReloadableResourceBundleMessageSource bundle;

	static{
		bundle = new ReloadableResourceBundleMessageSource();
	    bundle.setBasename(MESSAGE_BUNDLE_NAME);
	    bundle.setDefaultEncoding("UTF-8");
	}
	
	public static String get(final String key) {
		return bundle.getMessage(key, null, null);
	}
	
	public static String get(String key, Object...params ){
		return bundle.getMessage(key, params, null);
	}
	
	public static <T extends Enum<?>>String getEnumLabel(T enumValue){
		String key = enumValue.getClass().getSimpleName() + "." + enumValue.name();
		return bundle.getMessage(key, null, null);
	}

	public static String safeGet(String key){
		try{
			return get(key);
		}catch(NoSuchMessageException ex){
			log.error("key not found in bundle : " + key);
			return key;
		}
	}
}