package com.weblyzard.lib.string.nilsimsa;

import static org.junit.Assert.*;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 * Test the Nilsimsa algorithm
 * @author Albert Weichselbraun
 *
 */
public class NilsimsaTest {
	
	private final static String CONTENT_ENCODING = "ISO-8859-1";
	private static List<String> TEST_DATA = Arrays.asList( 
		"73302df80673894c115249b1f880abb1ec2b09f1c9726e642b690291e636fe6f c",
		"63f02df81323896c51419d71da922839ede04df1c920ff042b238750e61268ef hmac",
		"5fb02d781633a84ed3420ab1f2922931cca119f14bba6ce42f3b2010e23662ef java",
		"7bb42c980372a18f3113b1f5d29035a1fd2b79b5d9326c452b4023d0e212e14f lsh",
		"7fb024b80683894c751219b5d2882d316caa48e14bb0eec42f380290e67468ef md5",
		"cb3025980276894c63120890da8029b1446329f1c972f6642c292b91e636766f perl",
		"5b3025781212884ee90359b1fb842931ec2b39f1592264642f2d6a95e636f2eb python",
		"5e302d3002d2894ef30259b1f8902f31ec2b5bb14bd2664429386a91e272f26f ruby",
		"6bb024d80623884d51521df1da8c2121cca849e1cbf37e442f3d0a98e637606f sha1"
	);

	private static Map<String, String> testDocuments;

	public NilsimsaTest() {
		testDocuments = _readTestDocuments();
	}

	@Test
	public void compareTest() {
		Nilsimsa n1 = new Nilsimsa();
		n1.update("Hi Casey, can you send me the computation for this project?");
		System.out.println(n1.hexdigest());

		Nilsimsa n2 = new Nilsimsa();
		n2.update("Hello John, can you send me the computation for this project?");
		System.out.println(n2.hexdigest());

		System.out.println(n2.compare(n1));

	}

	private class HashPair {
		Nilsimsa hash;
		String description;
		HashPair(Nilsimsa h, String desc) {
			hash = h;
			description = desc;
		}
	}

	@Test
	public void testSimilarity() {
		ArrayList<HashPair> hashes = new ArrayList<>();
		for (String testData: TEST_DATA) {
			String[] testSet = testData.split(" ");
			try {
				URL resource = NilsimsaTest.class.getClassLoader().getResource("wiki-"+ testSet[1] + ".txt");
				String documentContent = FileUtils.readFileToString(new File( resource.getFile()), CONTENT_ENCODING);
				Nilsimsa n = new Nilsimsa();
				n.update(documentContent);
				hashes.add(new HashPair(n, testSet[1]));
			} catch (Exception e) {
				e.printStackTrace();
				fail("Cannot read corpus.");
			}
		}
		System.out.println("ok");
		for (int i=0; i < hashes.size(); i++) {
			HashPair p = hashes.get(i);
			for (int j=0; j < hashes.size(); j++) {
				if (i != j) {
					HashPair p2 = hashes.get(j);
					System.out.println("Compare " + p.description + " <-> " + p2.description + " = " + p.hash.compare(p2.hash) + " bits");
				}
			}
		}
	}

	@Test
	public void hashTest() {
		for (Map.Entry<String, String>testSet: testDocuments.entrySet()) {
			Nilsimsa n = new Nilsimsa();
			n.update( testSet.getValue() );
			System.out.println(n.hexdigest()+" "+testSet.getKey());
			assertEquals( testSet.getKey(), n.hexdigest( testSet.getValue()) );	
		}
	}
	
	/**
	 * compile test mapping
	 * @return a mapping of file content and the corresponding reference
	 *         Nilsimsa hash.
	 */
	private static Map<String, String> _readTestDocuments() {
		Map<String, String> result = new HashMap<> ();
		String[] testSet;
		String documentContent;
 		for (String testData: TEST_DATA) {
 			testSet = testData.split(" ");
 			try {
 				URL resource = NilsimsaTest.class.getClassLoader().getResource("wiki-"+ testSet[1] + ".txt");
				documentContent = FileUtils.readFileToString(
						new File( resource.getFile()), CONTENT_ENCODING);
				result.put( testSet[0], documentContent);
			} catch (IOException e) {
				e.printStackTrace();
				fail("Cannot read corpus.");
			}
		}
		return result;
	}
	

}
