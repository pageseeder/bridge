/*
 * This file is part of the PageSeeder Bridge API.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package org.pageseeder.bridge.util;

import java.util.Random;

/**
 * A simple class to generate sample data.
 *
 * @author Christophe Lauret
 * @version 0.1.0
 */
public class Sampler {

  /**
   * A list of first names to use.
   */
  private static final String[] FIRST_NAMES = new String[]{
    "Aada", "Abigail", "Ahmed", "Aino", "Alessandro", "Alex", "Alexa", "Alexander", "Ali", "Amanda",
    "Amelia", "Amelie", "Ana", "Anastasia", "Anders", "Anna", "Annie", "Aria", "Ariana", "Aubree",
    "Austin", "Ava", "Baptiste", "Ben", "Benjamin", "Beshoi", "Bilal", "Bill", "Caleb", "Camden",
    "Cameron", "Caroline", "Carter", "Charles", "Charlie", "Charlotte", "Chloe", "Chris", "Clara", "Clark",
    "Cooper", "Danica", "Daniel", "David", "Diego", "Dmitry", "Dora", "Drake", "Dylan", "Edith",
    "Edward", "Eli", "Elia", "Elijah", "Elin", "Elisabeth", "Elise", "Elizabeth", "Ella", "Ellen",
    "Elsa", "Ema", "Emerson", "Emilia", "Emily", "Emma", "Emmett", "Erin", "Eva", "Evan",
    "Fadi", "Farah", "Fatima", "Felicia", "Felicity", "Felix", "Finn", "Freja", "Frida", "Gabriel",
    "Gavin", "George", "Grace", "Griffin", "Gus", "Habib", "Habiba", "Hamza", "Hana", "Hanna",
    "Hannah", "Harper", "Harrison", "Harry", "Hassan", "Helmi", "Henry", "Hudson", "Hugo", "Hussein",
    "Ibrahim", "Ida", "Ines", "Irene", "Isaac", "Isabella", "Isaiah", "Isla", "Jace", "Jack",
    "Jackson", "Jacob", "Jade", "James", "Jase", "Jax", "Jean", "Jessica", "Joel", "Johannes",
    "John", "Jonah", "Jordyn", "Josefine", "Joseph", "Josh", "Juan", "Julia", "Julie", "Kai",
    "Kaiden", "Karim", "Keira", "Khaled", "Kingston", "Kinsley", "Kirollos", "Lachlan", "Lana", "Lara",
    "Laura", "Laurin", "Layla", "Lea", "Leandro", "Lena", "Leon", "Leonie", "Levi", "Lexi",
    "Lily", "Lina", "Lincoln", "Linnea", "Lola", "Loris", "Louise", "Lucas", "Lucie", "Lucija",
    "Madelyn", "Mahmoud", "Maja", "Malak", "Malcolm", "Mamadou", "Manon", "Manuel", "Marc", "Marco",
    "Marcus", "Mareva", "Maria", "Mariam", "Marie", "Marina", "Mark", "Markus", "Martina", "Marwa",
    "Mary", "Mason", "Matilda", "Matteo", "Mia", "Michael", "Mila", "Mina", "Minea", "Mohamed",
    "Molly", "Mustafa", "Mya", "Nada", "Nash", "Nathan", "Nathaniel", "Nick", "Niclas", "Nika",
    "Nikita", "Noah", "Noel", "Oliver", "Olivia", "Omar", "Paisley", "Parker", "Peter", "Petra",
    "Peyton", "Pierre", "Piper", "Quinn", "Rawiri", "Riley", "Robert", "Rodrigo", "Roland", "Ronja",
    "Ruby", "Ryan", "Ryder", "Sadie", "Saga", "Salma", "Samuel", "Sara", "Sarah", "Sawyer",
    "Scarlett", "Seth", "Siiri", "Simon", "Sofia", "Sophie", "Stella", "Summer", "Taha", "Tareq",
    "Tessa", "Teva", "Thomas", "Tilde", "Tim", "Tristan", "Venla", "Victoria", "Violet", "William",
    "Wilma", "Wyatt", "Xavier", "Yassin", "Youssef", "Zofia"
  };

  /**
   * A list of last names to use.
   */
  private static final String[] LAST_NAMES = new String[]{
    "Adams", "Agarwal", "Ahmed", "Al-Rawy", "Alawneh", "Ali", "Allen", "Anderson", "Andrews", "Arabiyat",
    "Armstrong", "Atkinson", "Bailey", "Baker", "Barker", "Barnes", "Bates", "Bawab", "Bell", "Bennett",
    "Bernard", "Berry", "Booth", "Borg", "Bradley", "Brooks", "Brown", "Butler", "Byrne", "Campbell",
    "Carr", "Carter", "Chambers", "Chapman", "Chen", "Choi", "Clark", "Clarke", "Cole", "Collins",
    "Cook", "Cooper", "Cox", "Cruz", "Cunningham", "Davies", "Davis", "Dawson", "Dean", "Diaz",
    "Dijkstra", "Dixon", "Dubois", "Dupont", "Edwards", "Ellis", "Evans", "Faizan", "Fakhouri", "Ferrari",
    "Fischer", "Fisher", "Foster", "Fournier", "Fox", "Fujiwara", "Garcia", "Gardner", "George", "Gibson",
    "Gill", "Gonzalez", "Gopal", "Gordon", "Graham", "Grant", "Gray", "Greco", "Green", "Griffiths",
    "Hall", "Hamilton", "Harper", "Harris", "Harrison", "Hart", "Harvey", "Hassan", "Hill", "Hoffmann",
    "Holmes", "Hong", "Horvat", "Hudson", "Hughes", "Hunt", "Hunter", "Hussein", "Ivanov", "Jackson",
    "James", "Jansen", "Jawaldeh", "Jbarat", "Jenkins", "Jensen", "Jeon", "Johansson", "Johnson", "Johnston",
    "Jones", "Kaur", "Kelly", "Kennedy", "Khan", "Kim", "King", "Klein", "Knight", "Ko",
    "Lambert", "Lane", "Lawrence", "Lawson", "Le", "Lee", "Leroy", "Lewis", "Li", "Liu",
    "Lloyd", "Lopez", "Macdonald", "Majali", "Malkawi", "Marshall", "Martin", "Mason", "Masuda", "Matthews",
    "Mcdonald", "Mehta", "Meyer", "Miller", "Mills", "Min", "Mishra", "Mitchell", "Moore", "Morales",
    "Moran", "Morgan", "Morris", "Mulder", "Muller", "Murphy", "Murray", "Nguyen", "Owen", "Palmer",
    "Parker", "Patel", "Pearce", "Pearson", "Perera", "Perez", "Peterson", "Petit", "Phillips", "Poole",
    "Powell", "Price", "Qatanani", "Ramirez", "Reid", "Reynolds", "Richards", "Richardson", "Roberts", "Robertson",
    "Robinson", "Rogers", "Romano", "Rose", "Ross", "Rossi", "Roy", "Russell", "Ryan", "Salem",
    "Sanchez", "Santos", "Saunders", "Schmidt", "Schneider", "Schulz", "Scott", "Shaaban", "Shahin", "Shaw",
    "Shimizu", "Shin", "Simpson", "Singhal", "Sloan", "Smirnov", "Smith", "Song", "Spencer", "Stevens",
    "Stewart", "Stone", "Suzuki", "Takahashi", "Tamm", "Tanaka", "Taylor", "Thomas", "Thompson", "Thomson",
    "Turner", "Walker", "Walsh", "Wang", "Ward", "Watson", "Watts", "Webb", "Weber", "Wells",
    "West", "White", "Wilkinson", "Williams", "Williamson", "Wilson", "Wood", "Wright", "Wu", "Yamada",
    "Yamamoto", "Yang", "Young", "Zahran", "Zhang"
  };

  /**
   * Random
   */
  private final Random random = new Random();

  /**
   * @return A random first name.
   */
  public String getRandomFirstName() {
    return FIRST_NAMES[this.random.nextInt(FIRST_NAMES.length)];
  }

  /**
   * @return A random last name.
   */
  public String getRandomLastName() {
    return LAST_NAMES[this.random.nextInt(LAST_NAMES.length)];
  }

  public int getRandomInt(int min, int max) {
    return min + this.random.nextInt(max-min);
  }

  public long getRandomLong(long min, long max) {
    return min + Math.round(Math.random()*(max-min));
  }

}



