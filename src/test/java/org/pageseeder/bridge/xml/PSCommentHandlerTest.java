/*
 * Copyright 2015 Allette Systems (Australia)
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
package org.pageseeder.bridge.xml;

import org.junit.Assert;
import org.junit.Test;
import org.pageseeder.bridge.model.PSComment;
import org.pageseeder.bridge.model.PSComment.Author;
import org.pageseeder.bridge.model.PSComment.Context;

/**
 * Test the comment handler
 */
public final class PSCommentHandlerTest {

  @Test
  public void testPassPublic1() throws Exception {
    PSCommentHandler handler = new PSCommentHandler();
    HandlerTests.parse("comment/comment-pass-public1.xml", handler);
    // Check that member is retrieved
    PSComment comment = handler.getComment();
    Assert.assertEquals(23504L, comment.getId().longValue());
    Assert.assertEquals("Simple public comment", comment.getTitle());
    Assert.assertEquals("This is a simple public comment", comment.getContent());
    Assert.assertEquals("text/plain", comment.getMediaType());
    // Author
    Author author = comment.getAuthor();
    Assert.assertNotNull(author);
    Assert.assertEquals("Somebody", author.name());
    Assert.assertNull(author.email());
    Assert.assertNull(author.member());
    // Context
    Context context = comment.getContext();
    Assert.assertNotNull(context);
    Assert.assertNotNull(context.group());
    Assert.assertEquals("comment-test", context.group().getName());
    Assert.assertNull(context.fragment());
    Assert.assertNull(context.uri());
    // Attachements
    Assert.assertNotNull(comment.getAttachments());
    Assert.assertTrue(comment.getAttachments().isEmpty());
    // Other semantics
    Assert.assertNull(comment.getType());
    Assert.assertNotNull(comment.getProperties());
    Assert.assertTrue(comment.getProperties().isEmpty());
    Assert.assertEquals("", comment.getPropertiesAsString());
    Assert.assertNotNull(comment.getLabels());
    Assert.assertTrue(comment.getLabels().isEmpty());
    Assert.assertFalse(comment.hasLabels());
    Assert.assertEquals("", comment.getLabelsAsString());
    // Task
    Assert.assertNull("jsmith", comment.getDue());
    Assert.assertNull("jsmith", comment.getAssignedTo());
    Assert.assertNull("jsmith", comment.getPriority());
    Assert.assertNull("jsmith", comment.getStatus());
  }

}
