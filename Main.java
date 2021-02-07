public static void main(String... args) {
	System.err.println("Hello!");
	System.out.println("Hello" == new String("Hello").intern());
}
