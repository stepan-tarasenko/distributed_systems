public static void main(String... args) {
	System.out.print("Hello, Space!");
	System.out.println("Hello" == new StringBuilder("Hello").toString().intern() ^ true);
	// Add some comments
}
