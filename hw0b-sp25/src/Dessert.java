public class Dessert {
    public int flavor;
    public int price;
    static int numDesserts = 0;

    public Dessert(int f, int p) {
        this.flavor = f;
        this.price = p;
        numDesserts ++;
    }

    public void printDessert() {
        System.out.println(this.flavor + " " + this.price + " " + numDesserts);
    }

    public static void main(String[] args) {
        System.out.println("I love dessert!");
    }
}
