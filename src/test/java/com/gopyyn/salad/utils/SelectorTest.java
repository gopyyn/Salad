package com.gopyyn.salad.utils;

import org.junit.Test;

import static com.gopyyn.salad.enums.SelectorType.CSS;
import static com.gopyyn.salad.utils.Selector.isCss;
import static org.assertj.core.api.Assertions.assertThat;

public class SelectorTest {
    @Test
    public void selector_WhenValidCSS_returnSelectorWithCssType() {
        Selector selector = new Selector("div input.some-class #some-id");
        assertThat(selector.getType()).isEqualTo(CSS);
    }

    @Test
    public void isCss_WhenValidCSSWithGreaterThanLessThan_returnTrue() {
        assertThat(isCss(".nav-pills>li.active<a")).isTrue();
    }

    @Test
    public void isCss_WhenValidCSSWithAttribute_returnTrue() {
        assertThat(isCss("input[target]")).isTrue();
        assertThat(isCss("input[type='text']")).isTrue();
        assertThat(isCss("input[type|='text']")).isTrue();
        assertThat(isCss("input[type$=\"text\"]")).isTrue();
        assertThat(isCss("input[type^='text']")).isTrue();
        assertThat(isCss("li[data-original-index='asdf-1']")).isTrue();
        assertThat(isCss("input[name^='vehicle.vin']")).isTrue();
    }
}