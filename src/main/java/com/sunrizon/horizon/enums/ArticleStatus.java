package com.sunrizon.horizon.enums;

/**
 * Defines the available statuses for an article.
 * 
 * REVIEW - Article is waiting for review/approval.
 * DRAFT - Article is saved as draft, not published yet.
 * PUBLISHED - Article is published and visible to readers.
 * ARCHIVED - Article is archived or taken offline, but not deleted.
 */
public enum ArticleStatus {

  /** Article is waiting for review/approval */
  REVIEW,

  /** Article is saved as draft, not published yet */
  DRAFT,

  /** Article is published and visible to readers */
  PUBLISHED,

  /** Article is archived or taken offline, but not deleted */
  ARCHIVED;
}
