package com.sunrizon.horizon.utils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/**
 * XSS防护工具类
 * <p>
 * 使用Jsoup库过滤HTML内容中的XSS攻击代码
 */
public class XssUtil {

  /**
   * 基础白名单：仅允许纯文本
   */
  private static final Safelist BASIC_SAFELIST = Safelist.none();

  /**
   * 宽松白名单：允许常见的安全HTML标签（用于富文本编辑器）
   * 包括：格式化标签、链接、图片、列表等
   */
  private static final Safelist RELAXED_SAFELIST = Safelist.relaxed()
      .addTags("h1", "h2", "h3", "h4", "h5", "h6")
      .addAttributes("a", "target", "rel")
      .addAttributes("img", "alt", "title")
      .addProtocols("a", "href", "http", "https", "mailto")
      .addProtocols("img", "src", "http", "https");

  /**
   * 自定义白名单：用于文章内容（更丰富的HTML支持）
   */
  private static final Safelist ARTICLE_SAFELIST = Safelist.relaxed()
      .addTags("h1", "h2", "h3", "h4", "h5", "h6",
          "pre", "code", "blockquote", "hr",
          "table", "thead", "tbody", "tr", "th", "td",
          "div", "span", "iframe")
      .addAttributes("a", "href", "title", "target", "rel")
      .addAttributes("img", "src", "alt", "title", "width", "height")
      .addAttributes("iframe", "src", "width", "height", "frameborder", "allowfullscreen")
      .addAttributes("pre", "class")
      .addAttributes("code", "class")
      .addAttributes("div", "class", "id")
      .addAttributes("span", "class", "style")
      .addAttributes("table", "class")
      .addAttributes("th", "scope")
      .addAttributes("td", "colspan", "rowspan")
      .addProtocols("a", "href", "http", "https", "mailto", "ftp")
      .addProtocols("img", "src", "http", "https", "data")
      .addProtocols("iframe", "src", "https");

  /**
   * 清理HTML内容（基础模式 - 移除所有HTML标签）
   *
   * @param html 原始HTML内容
   * @return 清理后的纯文本
   */
  public static String cleanBasic(String html) {
    if (html == null || html.isEmpty()) {
      return html;
    }
    return Jsoup.clean(html, BASIC_SAFELIST);
  }

  /**
   * 清理HTML内容（宽松模式 - 允许常见安全标签）
   *
   * @param html 原始HTML内容
   * @return 清理后的安全HTML
   */
  public static String cleanRelaxed(String html) {
    if (html == null || html.isEmpty()) {
      return html;
    }
    return Jsoup.clean(html, RELAXED_SAFELIST);
  }

  /**
   * 清理文章内容（文章模式 - 允许富文本标签）
   *
   * @param html 原始HTML内容
   * @return 清理后的安全HTML
   */
  public static String cleanArticle(String html) {
    if (html == null || html.isEmpty()) {
      return html;
    }
    return Jsoup.clean(html, ARTICLE_SAFELIST);
  }

  /**
   * 清理用户输入（评论、昵称等 - 仅保留纯文本）
   *
   * @param input 用户输入
   * @return 清理后的文本
   */
  public static String cleanUserInput(String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }
    // 移除所有HTML标签，只保留文本
    return Jsoup.clean(input, BASIC_SAFELIST);
  }

  /**
   * 转义HTML特殊字符
   *
   * @param html 原始字符串
   * @return 转义后的字符串
   */
  public static String escapeHtml(String html) {
    if (html == null || html.isEmpty()) {
      return html;
    }
    return html
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#x27;")
        .replace("/", "&#x2F;");
  }

  /**
   * 检查字符串是否包含潜在的XSS攻击代码
   *
   * @param input 待检查的字符串
   * @return true表示可能包含XSS代码
   */
  public static boolean containsXss(String input) {
    if (input == null || input.isEmpty()) {
      return false;
    }

    String lowerInput = input.toLowerCase();

    // 检查常见的XSS攻击模式
    String[] xssPatterns = {
        "<script", "javascript:", "onerror=", "onload=",
        "onclick=", "onmouseover=", "<iframe", "eval(",
        "alert(", "document.cookie", "window.location"
    };

    for (String pattern : xssPatterns) {
      if (lowerInput.contains(pattern)) {
        return true;
      }
    }

    return false;
  }
}
