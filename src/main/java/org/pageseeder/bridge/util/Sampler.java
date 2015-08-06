/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.pageseeder.bridge.model.PSMember;

/**
 * A simple class to generate sample data.
 *
 * @author Christophe Lauret
 * @version 0.3.7
 * @since 0.1.0
 */
public final class Sampler {

  /**
   * A list of first names to use.
   */
  private static String[] firstNames = null;

  /**
   * A list of last names to use.
   */
  private static String[] lastNames = null;

  /**
   * Random
   */
  private final Random random = new Random();

  /**
   * domain to use when generating
   */
  private String emailDomain = "sampler.local";

  /**
   * Set the email domain to use for the sampler.
   *
   * @param domain the email domain
   */
  public void setEmailDomain(String domain) {
    if (domain == null) throw new NullPointerException();
    if (domain.length() == 0) throw new IllegalArgumentException();
    this.emailDomain = domain;
  }

  /**
   * @return the email domain to use for the sampler
   */
  public String getEmailDomain() {
    return this.emailDomain;
  }

  /**
   * Returns a random first name from the internal list.
   *
   * @return A random first name.
   */
  public String nextFirstName() {
    if (firstNames == null) {
      firstNames = load("firstnames.txt");
    }
    return firstNames[this.random.nextInt(firstNames.length)];
  }

  /**
   * Returns a random last name from the internal list.
   *
   * @return A random last name.
   */
  public String nextLastName() {
    if (lastNames == null) {
      lastNames = load("lastnames.txt");
    }
    return lastNames[this.random.nextInt(lastNames.length)];
  }

  /**
   * Return an integer value between min and max.
   *
   * @param min The minimum value (inclusive)
   * @param max The max value (exclusive)
   *
   * @return the random long between min (included) and max (excluded)
   */
  public int nextInt(int min, int max) {
    return min + this.random.nextInt(max-min);
  }

  /**
   * Return a long value between min and max.
   *
   * @param min The minimum value (inclusive)
   * @param max The max value (exclusive)
   *
   * @return the random long between min (included) and max (excluded)
   */
  public long nextLong(long min, long max) {
    return min + Math.round(Math.random()*(max-min));
  }

  /**
   * Returns the next item in the list.
   *
   * @param list the list
   *
   * @param <T> The type of object in the list.
   *
   * @return a random item from the list.
   */
  public <T> T nextInList(List<T> list) {
    int i = this.random.nextInt(list.size());
    return list.get(i);
  }

  /**
   * Returns the next item in the array.
   *
   * @param array the array to take object from
   *
   * @param <T> The type of object in the array.
   *
   * @return a random item from the array.
   */
  public <T> T nextInArray(T[] array) {
    int i = this.random.nextInt(array.length);
    return array[i];
  }

  /**
   * Generate a new PageSeeder member with random names and email.
   *
   * @return a random PageSeeder member.
   */
  public PSMember nextMember() {
    PSMember member = new PSMember();
    String firstname = nextFirstName();
    String lastname = nextLastName();
    int no = nextInt(0, 10000);
    String email = (firstname+lastname+no).toLowerCase()+"@"+this.emailDomain;
    member.setFirstname(firstname);
    member.setSurname(lastname);
    member.setEmail(email);
    member.setUsername((firstname+lastname+no).toLowerCase());
    return member;
  }

  // private helpers
  // ----------------------------------------------------------------------------------------------

  /**
   * Loads the specified file from a resource in this package as a string array.
   *
   * @param filename the name of the resource load
   * @return each line of the filename as a string in an array
   *
   * @throws IllegalArgumentException If any error occurs.
   */
  private static String[] load(String filename) {
    URL url = Sampler.class.getResource(filename);
    if (url == null)
      throw new IllegalArgumentException(filename);
    InputStream in = null;
    try {
      in = url.openStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
      String name = reader.readLine();
      List<String> names = new ArrayList<String>();
      while (name != null) {
        names.add(name);
        name = reader.readLine();
      }
      return names.toArray(new String[]{});
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    } finally {
      try {
        in.close();
      } catch (IOException ex) {
        throw new IllegalArgumentException(filename);
      }
    }
  }

}
