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

  OK,

  DOCUMENT_DOCID_IS_TOO_LONG,

  DOCUMENT_TITLE_IS_TOO_LONG,

  DOCUMENT_LABELS_ARE_TOO_LONG,

  DOCUMENT_IS_A_FOLDER,

  FOLDER_IS_NOT_A_FOLDER,

  GROUP_NAME_IS_TOO_LONG,

  GROUP_OWNER_IS_TOO_LONG,

  GROUP_DESCRIPTION_IS_TOO_LONG,

  GROUP_DETAILTYPE_IS_TOO_LONG,

  GROUP_NAME_IS_INVALID,

  MEMBER_FIRSTNAME_IS_TOO_LONG,

  MEMBER_SURNAME_IS_TOO_LONG,

  MEMBER_USERNAME_IS_TOO_LONG,

  MEMBER_EMAIL_IS_TOO_LONG,

  DETAIL_FIELD_VALUE_IS_TOO_LONG,

}
