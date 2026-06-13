import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.MouseInfo;
import java.awt.*;
import java.security.Key;

public class DisplayPanel extends JPanel implements MouseListener, KeyListener, ActionListener, MouseMotionListener{
    public int fortesting;
    private boolean start;
    private int score;
    private int xpos;
    private int ypos;
    private int ranposx;
    private int ranposy;
    private double distance;
    private int radius;
    private int mousex;
    private int mousey;
    private Point mpos; //mouse pos
    private int timercount; //counts the ms for the ingame timer not the timer object (important)
    private Timer timer;
    private Timer clockTimer;
    private boolean gameend;
    private Tscore s;
    private int settime; //sets the amount of time u will have
    private boolean detectm1; //detects if player click (important)
    private Timer tracer;
    private int clickrate;
    private boolean m1cooldown;
    private Timer cooldown;
    private int ranXdisplacement;
    private int ranYdisplacement;
    private double clicks;
    private double hits;
    private int accuracy;
    private double idistance;
    private int bullseye;
    private boolean bullseyeactive;
    private int chanceofbullseye;
    private BufferedImage title;
    private BufferedImage pew;
    private BufferedImage background;
    private BufferedImage target;

    public DisplayPanel() {
        chanceofbullseye = 7; //it means 1 out of how many tries on average can u get a bullseye
        clickrate = 160;  //cool down to prevent spamming
        settime = 30;  //the time you are given
        timercount = 0;
        s = new Tscore(0);  //you can set points with this ig
        radius = 25;
        score = 0;
        try{
            title = ImageIO.read(new File("src/Title.png"));
        } catch (IOException e) {
            System.out.println("File for the variable \"title\" is not found");
        }
        try{
            pew = ImageIO.read(new File("src/pew.png"));
        } catch (IOException e){
            System.out.println("File for the variable \"pew\" is not found");
        }
        try{
            background = ImageIO.read(new File("src/background.jpg"));
        }catch (IOException e){
            System.out.println("File for the variable \"background\" is not found");
        }
        try{
            target = ImageIO.read(new File("src/target.png"));
        }catch (IOException e){
            System.out.println("File for the variable \"target\" is not found");
        }
        addMouseListener(this);
        addKeyListener(this);
        addMouseMotionListener(this);
        setFocusable(true); // this line of code + one below makes this panel active for keylistener events
        requestFocusInWindow(); // see comment above
        changepos();
        timer = new Timer(10, this);
        clockTimer = new Timer(1000, this);
        tracer = new Timer(75, this);
        cooldown = new Timer(clickrate, this);
        timer.start();
        clockTimer.start();
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        background(g);
        displaytarget(g);
        startscreen(g);
        checksgameend(g);
        if (detectm1){
            drawtracers(g);
        }
        if (start){
            drawwatergun(g);
        }
        displaysoundeff(g);
    }
    public void changepos(){ //randomizes the position of the target after getting clicked on
        ranposx = (int) (Math.random() * 461) + 250;
        ranposy = (int) (Math.random() * 341) + 60;
        xpos = ranposx;
        ypos = ranposy;
        requestFocusInWindow();
        bullseye();
    }
    @Override
    public void mouseClicked(MouseEvent e) {  // unimplemented

    }
    @Override
    public void mousePressed(MouseEvent e) { // unimplemented
        if (e.getButton() == MouseEvent.BUTTON1 && !detectm1 && !m1cooldown && start){
            detectm1 = true;
            m1cooldown = true;
            tracer.start();
            cooldown.start();
            ranXdisplacement = (int) (Math.random() * 61) - 120;
            ranYdisplacement = (int) (Math.random() * 20) - 38;
            clicks++;
        }
        if (inradofcircle()){
            hits++;
        }
    }
    @Override
    public void mouseReleased(MouseEvent e) {   //adds score if player clicks on target
        if (e.getButton() == MouseEvent.BUTTON1 && inradofcircle() && start){
            if (inradofinnercircle()){
                score += 2;
            }else{
                score++;
            }
            repaint();
            changepos();
        }
    }
    @Override
    public void mouseEntered(MouseEvent e) {// unimplemented

    }
    @Override
    public void mouseExited(MouseEvent e) { // unimplemented

    }
    @Override
    public void keyTyped(KeyEvent e) { // unimplemented

    }
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_M && !start && !gameend){ //turns the game on or off
            start = true;
            resetvar();
            changepos();
        }else if (keyCode == KeyEvent.VK_M && start){
            start = false;
            changepos();
        }else if (keyCode == KeyEvent.VK_Q && gameend){ //resets the game
            gameend = false;
            resetvar();
            changepos();
            if (start){
                start = false;
            }
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {   // unimplemented

    }
    public void timerlogic(){ //sets up the timer ui
        if (start == false){
            timercount = settime;
        }else if (start == true){
            timercount--;
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) { //DO NOT DELETE THIS METHOD
        if (e.getSource() == timer) { //checks constantly if the mouse is close enough to the target or nah (don't delete)
            inradofcircle();
            inradofinnercircle();
            repaint();
        }
        if (e.getSource() == clockTimer) {
            timerlogic();
            if (timercount <= -1){
                start = false;
                gameend = true;
                s.addScore(score);
                s.maxScore(score);
                timercount = 0;
            }
            repaint();
        }
        if (e.getSource() == tracer){
            detectm1 = false;
            accuracy = (int) (Math.round((hits * 100) / clicks));
            tracer.stop();
        }
        if (e.getSource() == cooldown){
            m1cooldown = false;
            cooldown.stop();
        }
        repaint();
    }
    @Override
    public void mouseDragged(MouseEvent e) {
    }
    @Override
    public void mouseMoved(MouseEvent e) { //returns mouse coords
        mpos = e.getPoint();
        mousex = (int) mpos.getX();
        mousey = (int) mpos.getY();
    }
    private boolean inradofcircle(){ // finds the distance between the center of the circle and mouse location
        double ramx = 0;
        double ramy = 0;
        ramx = Math.abs(mousex - radius - xpos);
        ramx = ramx * ramx;
        ramy = Math.abs(mousey - radius - ypos);
        ramy = ramy * ramy;
        distance = Math.sqrt(ramx + ramy);
        if (distance < radius){
            return true;
        }else {
            return false;
        }
    }
    private boolean inradofinnercircle(){ // finds the distance between the center of the circle and mouse location
        double ramx = 0;
        double ramy = 0;
        ramx = Math.abs(mousex - xpos - radius);
        ramx = ramx * ramx;
        ramy = Math.abs(mousey - radius - ypos);
        ramy = ramy * ramy;
        idistance = Math.sqrt(ramx + ramy);
        if (idistance <= radius / 4 && bullseyeactive){
            return true;
        }else {
            return false;
        }
    }
    private void startscreen(Graphics g){  // basically displays menu screen stuff
        int ram;
        if (!start && !gameend) { //for if start is false
            super.paintComponent(g);
            background(g);
            g.drawImage(title, 291 , 140, null);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
//            g.drawString(String.valueOf(mousex) + " " + String.valueOf(mousey), 400, 30);   (for testing)
            g.setFont(new Font("Arial", Font.PLAIN, 30));
            ram = g.getFontMetrics().stringWidth("Press \"m\" to start");
            g.drawString("Press \"m\" to start", (960 - ram) / 2, 260);
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("Total score: " + s.getTotalScore(), 5, 15);
            g.drawString("High Score: " + s.getMaxscore(), 5, 35);
        }
    }
    private void displayscore(Graphics g){ // basically what to display after the game ends
        FontMetrics ram;
        int ram1 = 0;
        super.paintComponent(g);
        background(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 30));
        ram1 =  g.getFontMetrics().stringWidth("Your accuracy was:    %");
        g.drawString("Your accuracy was: " + accuracy + "%", (960 - ram1) / 2, 180);
        ram1 =  g.getFontMetrics().stringWidth("Your score was:   ");
        g.drawString("Your score was: " + score, (960 - ram1) / 2, 220);
        ram1 =  g.getFontMetrics().stringWidth("Press \"q\" to go back to the menu");
        g.drawString("Press \"q\" to go back to the menu", (960 - ram1) / 2, 260);
        if (score < settime * 3 / 2){
            ram1 =  g.getFontMetrics().stringWidth("My grandmother can get a higher score than that, LOCK IN");
            g.drawString("My grandmother can get a higher score than that, LOCK IN", (960 - ram1) / 2,300);
        }else{
            ram1 =  g.getFontMetrics().stringWidth("Good job!");
            g.drawString("Good job!", (960 - ram1) / 2,300);
        }
    }
    private void checksgameend(Graphics g){ //just checks if game ends
        if (gameend){
            displayscore(g);
        }
    }
    private void displaytarget(Graphics g){ //what to display when the game is going on
        background(g);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
//        g.drawString(String.valueOf(mousex) + " " + String.valueOf(mousey), 400, 30);  (for testing)
        g.drawString("Score: " + score, 50, 30);
        g.drawString("Time: " + (timercount), 200, 30);
        g.drawString("Accuracy: " + accuracy + "%", 800, 30);
        g.drawString("Press \"m\" again to return to menu", 10,570);
        g.drawImage(target, xpos, ypos, null);
        g.drawImage(pew, 770, 370, null);
        g.setColor(Color.YELLOW);
        if (bullseyeactive){
            g.fillOval(xpos + (radius * 3 / 4) + 1,ypos + (radius * 3 / 4) + 1,radius / 2,radius / 2);
        }
        g.setColor(Color.RED);
        g.drawLine(mousex - 25, mousey, mousex - 6, mousey);
        g.drawLine(mousex + 6, mousey, mousex + 25, mousey);
        g.drawLine(mousex, mousey - 25, mousex, mousey - 6);
        g.drawLine(mousex, mousey + 6, mousex, mousey + 25);
    }
    public void resetvar(){ //resets some of the variables (used for calibration)
        timercount = settime;
        score = 0;
        hits = 0;
        clicks = 0;
        accuracy = 0;
        bullseyeactive = false;
    }
    public void drawtracers(Graphics g){ //draws the tracers for the gun or laser gun
        g.setColor(new Color(14, 136, 253, 255));
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(780, 410, mousex, mousey);
    }
    public void background(Graphics g){ //displays backgorund
        g.drawImage(background, 0 , 0, null);
    }
    public void displaysoundeff(Graphics g){  //displays *pew*
        if (m1cooldown){
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("*pew*", 850 + ranXdisplacement, 390 + ranYdisplacement);
        }
    }
    public void bullseye(){  //basically the crit hit function
        bullseye = (int) (Math.random() * chanceofbullseye) + 1;
        if (bullseye > chanceofbullseye - 1){
            bullseyeactive = true;
        }else{
            bullseyeactive = false;
        }
    }
    private void drawwatergun(Graphics g){  //displays the water gun
        g.drawImage(pew, 770, 370, null);
    }
}