package com.lin.dynamictask.constants;

import java.util.regex.Pattern;

/**
 * 正则表达式常量
 *
 * @author 林维家
 * @since 2025/5/21 11:51
 */
public class PatternConstants {

    /**
     * 匹配${...}
     */
    public static final Pattern $ = Pattern.compile("^\\$\\{.+}$");

    private PatternConstants() {

    }
}
