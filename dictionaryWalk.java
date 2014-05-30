// Author: Daniel Lustig
// 5/23/14
// Dictionary Walk
// 'Dodgson doublet' with the inclusion of adjacent words found from adding or removing a single letter


import java.io.*;
import java.util.*;

class Node {
	int index;
	Node next;
	Node (int i, Node t) {
		index = i; 
		next = t; 
		}
}

class Object {
	public String word;
	public int index;
	public Node link;
	public boolean linked;
	public int visited;
	public Object prev;

	Object (String w, int i) {
		word = w; 
		index = i;
		link = null; 
		linked = false; 
		visited = 0; 
		prev = null; 
	}

	public String toString () {
		return word; 
		}
}

public class dictionaryWalk {
	
	static final int maxWords = 200000;
	static final Object dictionary[] = new Object[maxWords];

	static int N = 0;
	static int visited_max = 0;

	static void print_Solution (Object object) {
		while (object != null && object.visited == visited_max) {
			System.out.println (object.word);
			object = object.prev;
		}
		return;
	}

	// This function loads the dictionary into a single array
	static void loadDictionary () {
		FileReader fr = null;
		BufferedReader br = null;

		int index = 0;
		String word;
		Object object;

		try {
			//create a file reader and buffer reader
			fr = new FileReader("TWL06.txt"); 
			br = new BufferedReader(fr); 

			//while the next line is not null, continue reading
			while ((word = br.readLine()) != null) {
				//create a new object of the word being read, and capitalize it
				object = new Object (word.toUpperCase (), index);
				//add the word into the dictionary array
				dictionary[index] = object;
				index = index + 1;
			}
			System.out.print ('\n');
			N = index;

			//catch I/O errors
		} catch (IOException e) {
			System.out.println("Error: loadDictionary: " + e.getMessage());
		} finally {
			//close the buffer reader when word has been read in
			if (br != null) { 
				try { 
					br.close();
					} catch (IOException ioe) {/*don't do anything when an error has been caught*/}
			}
		}
	}

	// This is a simple function which implements a binary search
	static Object word_search (String word) {
		Object object = null;
		int left = 0;
		int right = N - 1;
		int c;
		int middle;

		// While the left index is less than or equal to the right
		// then there is still words to match in the dictionary file
		while (left <= right) {
			//find the middle of the dictionary file by taking the index of the first object and last object and dividing it by 2
			middle = (left + right) / 2;
			object = dictionary[middle];

			//compare the passed word and the indexed "middle" word of the dictionary
			c = word.compareTo (object.word);
			
			// If the passed word is greater than 0 (greater than the "middle" word) than search only
			// the second half of the dictionary, or the right most part. 
			if (c > 0) {
				//set the new left index at one spot past the middle on the right
				//example: 0-100. 50 is the middle. 51 is set to the new left index
				left = middle + 1;
				continue; 
				}
			// If the passed word is less than 0 (less than the "middle" word) than search only
			// the first half of the dictionary, or the left most part. 
			if (c < 0) {
				//set the new right index at one spot past the middle on the left
				//example: 0-100. 50 is the middle. 49 is set to the new right index
				right = middle - 1;
				continue;
				}
			return object;
		}

		return null;
	}

	// This function finds all adjacent words to the original word, and then links them to the original.
	static Node words_Adjacent_To (String word) {
		char c0;
		char wordArray[] = word.toCharArray ();
		String word_new;
		int i;
		int wordLength = word.length();

		Object object = null;
		Node link = null;

		//**** This function changes one letter in the original word at each iteration. The length does not change
		// The original word is used at each iteration, thus each "new" word is only one character different
		// from the original word.
		// When a new valid word has been found a new node is created and a link is made between the original and new node.
		// Example: (original: AAA) - BAA | ABA | AAB - CAA | ACA | AAC...
			for (i = 0; i < wordLength; i++) {
				c0 = word.charAt(i);
				for (char x = 'A'; x <= 'Z'; x++) {
					//if char x is not the same as the character being changed in word then replace the letter.
					if (x != c0) {
						wordArray[i] = x;
						//check to see if the new word is valid in the dictionary file.
						word_new = new String (wordArray);
						object = word_search (word_new);
						if (object != null) {
							//System.out.println(lengthDifference + " : " + word_new);
							link = new Node (object.index, link);
						}
					}
				}
				wordArray[i] = c0;
			}

			//**** This function adds one letter to the original word at each iteration
			// The original word is again used at each iteration, thus each "new" word is still only one character different
			// When a new valid word has been found a new node is created and a link is made between the original and new node.
			//Example: (original: AAA) - AAAA | BAAA | CAAA - ABAA | ACAA...
			for (i = 0; i < wordLength; i++) {
				for (char x = 'A'; x <= 'Z'; x++) {
					word_new = new String(word.substring(0, i) + x + word.substring(i, word.length()));
					object = word_search (word_new);
					if (object != null) {
						//System.out.println(lengthDifference + " : " + word_new);
						link = new Node (object.index, link);
					}
				}
			}

			//**** This function removes a single letter at each iteration of the original word
			// When a new valid word has been found a new node is created and a link is made between the original and new node.
			//Example: (original: ABC) - AB | AC | BC
			for (i = 0; i < wordLength; i++) {
				word_new = new String(word.substring(0, i) + word.substring(i + 1));
				object = word_search (word_new);
				if (object != null) {
					//System.out.println(lengthDifference + " : " + word_new);
					link = new Node (object.index, link);
				}
			}
		return link;
	}

	// This function checks and returns whether two objects are equal to each other.
	public static boolean word_Equal (Object word1, Object word2) {
		return (word1.index == word2.index);
	}

	// This function implements a breadth first search of all words in the node tree
	// Essentially it checks each node going from left to right on each level before continuing.
	public static void breadth_First_Search (String head, String tail) {
		Object object_root = new Object ("", -1);
		Object object, object_Next;
		Node link;

		LinkedList<Object> q = new LinkedList<Object> ();

		//Check that both the head and tail words are valid words in the dictionary
		Object head_object = word_search (head.toUpperCase());
		if (head_object == null) {
			System.out.println (head + " is not a legal word in the dictionary.");
			return;
		}
		
		Object tail_object = word_search (tail.toUpperCase());
		if (tail_object == null) {
			System.out.println (tail + " is not a legal word in the dictionary.");
			return;
		}

		visited_max++;

		object_root.visited = visited_max;
		head_object.prev = object_root;
		q.offer (head_object);

		while ((object = q.poll ()) != null) {

			if (object.visited < visited_max) {
				object.visited = visited_max;
			}
			else {
				continue;
			}

			// Check whether the current object is equal to the tail_object, thus a solution has been found.
			// If so, then print the solution
			if (word_Equal (object, tail_object)) {
				System.out.println ("An adjacent to word list between " + tail + "/" + head + " has been found.");
				print_Solution (object);
				return;
			}
			// If the object does not have any links to any other objects then pass to words_Adjacent_To and attempt to find at least 1
			if (!object.linked) {
				//System.out.println(object.word);
				object.link = words_Adjacent_To (object.word);		
				object.linked = true;
			}
			link = object.link;
			
			// While the object has a link to another object
			while (link != null) {
				object_Next = dictionary[link.index];
				if (object_Next.prev == null || object_Next.prev.visited < visited_max){
					object_Next.prev = object; 
				}
					q.offer (object_Next);
					link = link.next;
			}
		}

		System.out.println (tail + "/" + head + " cannot be connected via adjacent to words.");
		return;
	}

	public static void main (String argv[]) {
		String word1;
		String word2;


		System.out.println ("Reading in the dictionary...");
		loadDictionary ();
		System.out.println ("Dictionary has been read");

		
		// create a Scanner object
		Scanner In = new Scanner(System.in);
		
		// create an infinite loop of game play.
		while (true) {
			System.out.print ("Please input the first word: ");
			word1 = In.nextLine ();
			if (word1.length () == 0) {
				break; 
				}

			System.out.print ("Please input the second word: ");
			word2 = In.nextLine ();
			if (word2.length () == 0) {
				break; 
				}

			// Word2 is passed to the breadth_First_Search function before word1 because the solution is kept
			// in a first in - last out stack. All permutations of word2 will be found before word1.
			breadth_First_Search (word2, word1);
			
		}
		In.close();

		return;
	}
}

