public static void main(String... args) {
	System.out.print("Hello, Space!");
	System.out.println("Hello" != new String("Helo").toString().intern());
}
