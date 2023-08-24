package com.gopyyn.salad.utils;

import com.gopyyn.salad.enums.SelectorType;
import org.openqa.selenium.By;

import java.util.regex.Pattern;

import static com.gopyyn.salad.enums.SelectorType.*;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.substringAfter;

public class Selector {
    private static final Pattern CSS_PATTERN = Pattern.compile("((((abbr|acronym|address|applet|area|article|aside|audio|base|basefont|bdi|bdo|big|blockquote|body|br|button|canvas|center|cite|code|col|cogroup|datalist|data|dd|del|details|dfn|dialog|dir|div|dl|dt|embed|em|fieldset|figcaption|figure|font|footer|form|frameset|frame|header|head|html|iframe|img|input|ins|kbd|label|legend|link|li|main|map|mark|meta|meter|nav|noframes|noscript|object|ol|optgroup|option|output|parm|picture|pre|progress|rp|rt|ruby|samp|script|section|select|small|source|span|strike|strong|style|sub|summary|sup|svg|table|tbody|td|template|txtarea|tfoot|thead|th|time|title|track|tr|tt|ul|var|video|wbr|a|b|i|p|r|s|q|u)\\b)|[\\.#@\\+:\\-][\"']?\\w+[\"']?)(\\[[a-zA-Z0-9_\\-]+([|^$*]?=[\"'][a-zA-Z0-9_ \\.\\-]+[\"'])?\\])?|[ <>&])+");
    public static final String LINK_SELECTOR = "link=";
    public static final String CSS_SELECTOR = "css=";
    public static final String CLASS_SELECTOR = "class=";
    public static final String NAME_SELECTOR = "name=";
    private SelectorType type;
    private String expression;
    private By by;

    public Selector(String expression) {
        this.expression = expression;
        if (isXpath(expression)) {
            this.type = XPATH;
            this.by = By.xpath(expression);
        } else if (isName(expression)) {
            this.type = NAME;
            this.by = By.name(substringAfter(expression, NAME_SELECTOR));
        } else if (isClassName(expression)) {
            this.type = CLASS;
            this.by = By.className(substringAfter(expression, CLASS_SELECTOR));
        } else if (isLinkText(expression)) {
            this.type = SelectorType.LINK;
            this.by = By.linkText(substringAfter(expression, LINK_SELECTOR));
        } else if (isCss(expression)){
            this.type = CSS;
            String exp = expression.contains(CSS_SELECTOR)?substringAfter(expression, CSS_SELECTOR):expression;
            this.by = By.cssSelector(exp);
        } else {
            this.type = TEXT;
            this.by = By.xpath(format("//*[normalize-space(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ\u00A0','abcdefghijklmnopqrstuvwxyz'))='%1$s'] | //input[translate(@value,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz') = '%1$s']", expression.trim().toLowerCase()));
        }
    }

    public static final boolean isCss(String expression) {
        return expression.startsWith(CSS_SELECTOR) || CSS_PATTERN.matcher(expression).matches();
    }

    private boolean isLinkText(String expression) {
        return expression.startsWith(LINK_SELECTOR);
    }

    private boolean isClassName(String expression) {
        return expression.startsWith(CLASS_SELECTOR);
    }

    private boolean isName(String expression) {
        return expression.startsWith(NAME_SELECTOR);
    }

    private boolean isXpath(String text) {
        return text.startsWith("/");
    }

    public SelectorType getType() {
        return type;
    }

    public String getExpression() {
        return expression;
    }

    public By getBy() {
        return by;
    }
}
