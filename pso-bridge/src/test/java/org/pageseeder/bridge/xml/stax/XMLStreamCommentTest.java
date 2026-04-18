/*
 * Copyright 2017 Allette Systems (Australia)
 * http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pageseeder.bridge.xml.stax;

import org.junit.Test;
import org.pageseeder.bridge.core.*;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.time.OffsetDateTime;

import static org.junit.Assert.*;

public final class XMLStreamCommentTest {

  /**
   *
   * <comment id="25061" discussionid="25061" contentrole="Comment" created="2016-12-20T16:03:52+11:00">
   *   <title>findSimpleComment</title>
   *   <author id="92" firstname="Unit" surname="Test" username="unit-admin" status="activated">
   *     <fullname>Unit Test</fullname>
   *   </author>
   *   <content type="text/plain">create Simple Comment</content>
   *   <context>
   *     <group id="335" name="unittest-comment-findcomment" description="For unit testing" owner="unittest" access="member" common="false" />
   *   </context>
   * </comment>
   */
  @Test
  public void testCommentGroup() throws IOException, XMLStreamException {
    Comment comment = XMLStreamTest.parseItem("comment/comment-pass-group.xml", new XMLStreamComment());
    assertEquals(25061, comment.getId());
    assertEquals(25061, comment.getDiscussionId());
    assertEquals("Comment", comment.getContentRole());
    assertEquals(OffsetDateTime.parse("2016-12-20T16:03:52+11:00"), comment.getCreated());

    // Content
    assertEquals("create Simple Comment", comment.getContentAsString());
    assertEquals("text/plain", comment.getContentType());

    // Context
    assertNotNull(comment.getContext());
    Group exp = new Group(335, new GroupName("unittest-comment-findcomment"), "", "For unit testing", "unittest");
    Group got = comment.getContext().getGroup();
    BasicGroupTest.assertEquals(exp, got);

    // Attachments
    assertFalse(comment.hasAttachments());
    assertNotNull(comment.getAttachments());
    assertTrue(comment.getAttachments().isEmpty());

    // Labels
    assertFalse(comment.hasLabels());
    assertEquals(LabelList.NO_LABELS, comment.getLabels());

    // Author
    assertNotNull(comment.getAuthor());
// TODO



    // Not specified
    assertNull(comment.getModified());
    assertNull(comment.getAssignedTo());
    assertNull(comment.getDue());
    assertFalse(comment.isDraft());
    assertFalse(comment.isModerated());
  }

  @Test
  public void testCommentNasty() throws IOException, XMLStreamException {
    Comment comment = XMLStreamTest.parseItem("comment/comment-pass-nasty.xml", new XMLStreamComment());
  }

  @Test
  public void testCommentTask() throws IOException, XMLStreamException {
    Comment comment = XMLStreamTest.parseItem("comment/comment-pass-task.xml", new XMLStreamComment());
  }

  @Test
  public void testCommentTaskType() throws IOException, XMLStreamException {
    Comment comment = XMLStreamTest.parseItem("comment/comment-pass-task-type.xml", new XMLStreamComment());
  }

  @Test
  public void testCommentTaskTypeUri() throws IOException, XMLStreamException {
    Comment comment = XMLStreamTest.parseItem("comment/comment-pass-task-type-uri.xml", new XMLStreamComment());
  }

  @Test
  public void testCommentTaskUri() throws IOException, XMLStreamException {
    Comment comment = XMLStreamTest.parseItem("comment/comment-pass-task-uri.xml", new XMLStreamComment());
  }

  @Test
  public void testCommentType() throws IOException, XMLStreamException {
    Comment comment = XMLStreamTest.parseItem("comment/comment-pass-type.xml", new XMLStreamComment());
  }

  @Test
  public void testCommentTypeUri() throws IOException, XMLStreamException {
    Comment comment = XMLStreamTest.parseItem("comment/comment-pass-type-and-uri.xml", new XMLStreamComment());
  }

  @Test
  public void testCommentUri() throws IOException, XMLStreamException {
    Comment comment = XMLStreamTest.parseItem("comment/comment-pass-uri.xml", new XMLStreamComment());
  }

  @Test
  public void testCommentAttachments() throws IOException, XMLStreamException {
    Comment comment = XMLStreamTest.parseItem("comment/comment-pass-attachments.xml", new XMLStreamComment());
//    Assert.assertNotNull(uri);
//    Assert.assertEquals(123456L, uri.getId());
//    Assert.assertFalse(uri.isExternal());
//    Assert.assertFalse(uri.isFolder());
//    Assert.assertEquals("https", uri.getScheme());
//    Assert.assertEquals("ps.example.com", uri.getHost());
//    Assert.assertEquals(443, uri.getPort());
//    Assert.assertEquals("/ps/test/acme/documents/hello%20world.psml", uri.getPath());
//    Assert.assertEquals("/ps/test/acme/documents/hello world.psml", uri.getDecodedPath());
//    Assert.assertEquals("Hello World!", uri.getTitle());
//    Assert.assertEquals("application/vnd.pageseeder.psml+xml", uri.getMediaType());
//    Assert.assertEquals("Hello World!", uri.getDisplayTitle());
//    Assert.assertEquals(OffsetDateTime.parse("2008-03-17T17:11:18+11:00"), uri.getCreatedDate());
//    Assert.assertEquals(OffsetDateTime.parse("2017-01-03T14:45:21+11:00"), uri.getModifiedDate());
//    Assert.assertEquals("HELLO_WORLD", uri.getDocid());
  }

}
