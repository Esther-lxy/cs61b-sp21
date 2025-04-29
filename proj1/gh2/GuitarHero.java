package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    public static final double CONCERT_A = 440.0;
    public static final int notes = 37;
    public static final String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

    public static void main(String[] args){
        GuitarString[] guitars = new GuitarString[notes];
        for (int i = 0; i < 37; i++) {
            guitars[i] = new GuitarString(CONCERT_A * Math.pow(2, (i - 24)/12));
        }

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int toPlay = keyboard.indexOf(key);
                guitars[toPlay].pluck();
            }

            double sample = 0;
            for (int i = 0; i < notes; i++ )
            {
                sample = sample + guitars[i].sample();
            }

            StdAudio.play(sample);

            for (int i = 0; i < notes; i++) {
                guitars[i].tic();
            }
        }
    }
}
