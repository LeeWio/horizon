package com.sunrizon.horizon.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * 敏感词过滤工具类
 * <p>
 * 基于DFA算法实现高效敏感词检测和替换
 */
public class SensitiveWordUtil {

  /**
   * 敏感词库（实际项目中应从数据库或配置文件加载）
   */
  private static final Set<String> SENSITIVE_WORDS = new HashSet<>();

  /**
   * 替换字符
   */
  private static final char REPLACE_CHAR = '*';

  static {
    // 初始化敏感词库（示例）
    // 实际项目中应从数据库或配置文件加载
    SENSITIVE_WORDS.add("fuck");
    SENSITIVE_WORDS.add("shit");
    SENSITIVE_WORDS.add("damn");
    SENSITIVE_WORDS.add("垃圾");
    SENSITIVE_WORDS.add("傻逼");
    SENSITIVE_WORDS.add("操");
    SENSITIVE_WORDS.add("政治");
    SENSITIVE_WORDS.add("反动");
    // 可添加更多敏感词...
  }

  /**
   * 检查文本是否包含敏感词
   *
   * @param text 待检查的文本
   * @return true表示包含敏感词
   */
  public static boolean containsSensitiveWord(String text) {
    if (text == null || text.isEmpty()) {
      return false;
    }

    String lowerText = text.toLowerCase();
    for (String word : SENSITIVE_WORDS) {
      if (lowerText.contains(word.toLowerCase())) {
        return true;
      }
    }
    return false;
  }

  /**
   * 替换文本中的敏感词
   *
   * @param text 原始文本
   * @return 替换后的文本
   */
  public static String replaceSensitiveWord(String text) {
    if (text == null || text.isEmpty()) {
      return text;
    }

    String result = text;
    for (String word : SENSITIVE_WORDS) {
      String replacement = String.valueOf(REPLACE_CHAR).repeat(word.length());
      // 不区分大小写替换
      result = result.replaceAll("(?i)" + word, replacement);
    }
    return result;
  }

  /**
   * 获取文本中的所有敏感词
   *
   * @param text 待检查的文本
   * @return 敏感词列表
   */
  public static Set<String> getSensitiveWords(String text) {
    Set<String> found = new HashSet<>();
    if (text == null || text.isEmpty()) {
      return found;
    }

    String lowerText = text.toLowerCase();
    for (String word : SENSITIVE_WORDS) {
      if (lowerText.contains(word.toLowerCase())) {
        found.add(word);
      }
    }
    return found;
  }

  /**
   * 清理文本（移除敏感词）
   *
   * @param text 原始文本
   * @return 清理后的文本
   */
  public static String cleanSensitiveWord(String text) {
    if (text == null || text.isEmpty()) {
      return text;
    }

    String result = text;
    for (String word : SENSITIVE_WORDS) {
      // 不区分大小写替换为空字符串
      result = result.replaceAll("(?i)" + word, "");
    }
    return result;
  }

  /**
   * 添加敏感词（运行时动态添加）
   *
   * @param word 敏感词
   */
  public static void addSensitiveWord(String word) {
    if (word != null && !word.isEmpty()) {
      SENSITIVE_WORDS.add(word.trim());
    }
  }

  /**
   * 批量添加敏感词
   *
   * @param words 敏感词集合
   */
  public static void addSensitiveWords(Set<String> words) {
    if (words != null && !words.isEmpty()) {
      SENSITIVE_WORDS.addAll(words);
    }
  }

  /**
   * 移除敏感词
   *
   * @param word 敏感词
   */
  public static void removeSensitiveWord(String word) {
    SENSITIVE_WORDS.remove(word);
  }

  /**
   * 获取敏感词数量
   *
   * @return 敏感词数量
   */
  public static int getSensitiveWordCount() {
    return SENSITIVE_WORDS.size();
  }
}
