public class Tscore { //This class tracks total score and highest scores
    private int totalscore;
    private int maxscore;

    public Tscore(int initalscore){
        totalscore = initalscore;
    }
    public int getTotalScore(){
        return totalscore;
    }
    public void addScore(int score){
        totalscore += score;
    }
    public void maxScore(int score){
        if (score > maxscore){
            maxscore = score;
        }
    }
    public int getMaxscore(){
        return maxscore;
    }
}