package org.pageseeder.bridge.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.pageseeder.bridge.util.Rules;

/**
 * Define options to send mail via PageSeeder.
 *
 * @author Christophe Lauret
 * @version 0.2.4
 * @since 0.2.4
 */
public final class MailOptions {

  public enum Notify {

    /**
     * Send to all members of the group following their notification settings.
     */
    normal,

    /**
     * Send to all members of the group ignoring their notification settings.
     */
    announce,

    /**
     * Send to member currently logged in.
     */
    self,

    /**
     * Send to the email addresses specified in the parameter <code>recipients</code>.
     */
    recipients;

  }

  /**
   * The email template to use.
   */
  public enum Template {

    auto_responder,
    change_password,
    change_email_confirm,
    accept_comment,
    new_member,
    new_url,
    new_comment,
    new_version,
    out_of_office_change,
    out_of_office_warning,
    reject_comment,
    reset_password_confirm,
    reset_password_complete,
    membership_accept,
    membership_confirm,
    membership_new_member,
    membership_complete,
    daily_digest,
    external;

    private final String _template;

    /**
     *
     */
    private Template() {
      this._template = name().toLowerCase().replace('_', '-');
    }

    /**
     * @return the _template
     */
    public String template() {
      return this._template;
    }

  }


  /**
   * The email template.
   */
  private Template template = Template.external;

  /**
   * The content to be send.
   */
  private String content = null;

  /**
   * The Email notify
   */
  private Notify notify = Notify.normal;

  /**
   * The list of recipients.
   */
  private List<String> recipients = null;

  /**
   * The list of attachments
   */
  private List<String> attachments = null;

  /**
   * Creates a new set of mail options.
   */
  public MailOptions() {
  }

  /**
   * @return the content
   */
  public String getContent() {
    return this.content;
  }

  /**
   * @param content the content to set
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * @return the template
   */
  public Template getTemplate() {
    return this.template;
  }

  /**
   * @param template the template to set
   */
  public void setTemplate(String template) {
    this.template = Template.valueOf(template.toLowerCase().replace('-', '_'));
  }

  /**
   * @param template the template to set
   */
  public void setTemplate(Template template) {
    if (template == null) throw new NullPointerException("Template must be specified");
    this.template = template;
  }

  /**
   * @return the notify
   */
  public Notify getNotify() {
    return this.notify != null ? this.notify : Notify.normal;
  }

  /**
   * @param notify the notify to set
   */
  public void setNotify(Notify notify) {
    this.notify = notify;
  }

  /**
   * Indicate whether the recipients have been specified.
   *
   * @return <code>true</code> if there is at least one recipient;
   *         <code>false</code> otherwise.
   */
  public boolean hasRecipients() {
    return this.recipients != null && this.recipients.size() > 0;
  }

  /**
   * @return the recipients
   */
  public List<String> getRecipients() {
    return this.recipients != null ? Collections.unmodifiableList(this.recipients) : Collections.<String>emptyList();
  }

  /**
   * @return the recipients
   */
  public String getRecipientsAsString() {
    return asString(getRecipients());
  }

  /**
   * @param email the email address to add to the list of recipient.
   */
  public void addRecipients(String email) {
    if (this.recipients == null)
      this.recipients = new ArrayList<String>();
    if (Rules.isEmail(email)) {
      this.recipients.add(email);
    } else {
      throw new IllegalArgumentException("Recipient is not a valid email address:"+email);
    }
  }

  /**
   * Indicate whether the attachments have been specified.
   *
   * @return <code>true</code> if there is at least one attachment;
   *         <code>false</code> otherwise.
   */
  public boolean hasAttachments() {
    return this.attachments != null && this.attachments.size() > 0;
  }

  /**
   * @return the list of attachments
   */
  public List<String> getAttachments() {
    return this.attachments != null? Collections.unmodifiableList(this.attachments) : Collections.<String>emptyList();
  }

  /**
   * @return the attachments
   */
  public String getAttachmentsAsString() {
    return asString(getAttachments());
  }

  /**
   * @param url the URL to the attachment to add.
   */
  public void addAttachment(String url) {
    if (this.attachments == null)
      this.attachments = new ArrayList<String>();
    // TODO check URL
    this.attachments.add(url);
  }

  /**
   * Returns the list of string as a comma separated list.
   *
   * @param list the list to stringify
   *
   * @return the list as a comma separated list.
   */
  private static String asString(List<String> list) {
    StringBuilder s = new StringBuilder();
    for (String item : list) {
      if (s.length() > 0) s.append(',');
      s.append(item);
    }
    return s.toString();
  }
}
