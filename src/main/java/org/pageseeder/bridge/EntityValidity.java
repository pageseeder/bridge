/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge;

/**
 * Possible states for entity validity.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public enum EntityValidity {

  /**
   * The entity appears valid.
   */
  OK,

  /**
   * The document ID is too long, max allowed is 100.
   */
  DOCUMENT_DOCID_IS_TOO_LONG,

  /**
   * The document title is too long, max allowed is 250.
   */
  DOCUMENT_TITLE_IS_TOO_LONG,

  /**
   * The document labels are too long.
   */
  DOCUMENT_LABELS_ARE_TOO_LONG,

  /**
   * The URI is folder not a document and therefore cannot be represented as a <code>PSDocument</code> instance.
   */
  DOCUMENT_IS_A_FOLDER,

  /**
   * The URI is a document not a folder and therefore cannot be represented as a <code>PSFolder</code> instance.
   */
  FOLDER_IS_NOT_A_FOLDER,

  /**
   * The full name of the group is too long, max allowed is 60.
   */
  GROUP_NAME_IS_TOO_LONG,

  /**
   * The full name of the group is too long, max allowed is 100.
   */
  GROUP_OWNER_IS_TOO_LONG,

  /**
   * The description of the group is too long, max allowed is 250.
   */
  GROUP_DESCRIPTION_IS_TOO_LONG,

  /**
   * The detail type of the group is too long, max allowed is 150.
   */
  GROUP_DETAILTYPE_IS_TOO_LONG,

  /**
   * The template of the group is too long, max allowed is 60.
   */
  GROUP_TEMPLATE_IS_TOO_LONG,

  /**
   * The name of the group is either reserved or uses and invalid character.
   */
  GROUP_NAME_IS_INVALID,

  /**
   * The first name of the member is too long, max allowed is 20.
   */
  MEMBER_FIRSTNAME_IS_TOO_LONG,

  /**
   * The last name of the member is too long, max allowed is 20.
   */
  MEMBER_SURNAME_IS_TOO_LONG,

  /**
   * The username of the member is too long, max allowed is 100.
   */
  MEMBER_USERNAME_IS_TOO_LONG,

  /**
   * The email address of the member is too long, max allowed is 100.
   */
  MEMBER_EMAIL_IS_TOO_LONG,

  /**
   * The value of the details field in the membership is too long, max allowed is 100.
   */
  DETAIL_FIELD_VALUE_IS_TOO_LONG,

}
