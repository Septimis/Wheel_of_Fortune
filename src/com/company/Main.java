package com.company;


import javafx.application.Application;

import java.sql.Time;
import java.util.concurrent.TimeUnit;
import javafx.beans.value.ObservableListValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Main extends Application
{
    private ArrayList<String> fileContents = new ArrayList<>();
    private ObservableList<Line> wordBase = FXCollections.observableArrayList();
    private ObservableList<Text> lettersInPhrase = FXCollections.observableArrayList();
    private ObservableList<Text> scoreValues = FXCollections.observableArrayList();
    private ObservableList<Point> scoreValuesXY = FXCollections.observableArrayList();
    private Circle wheel = new Circle();
    private String thePhrase;
    private String wrongGuesses = "";
    private int victoryIncrement = 0;
    private int numWrongGuesses = 0;
    private boolean inReset = true;
    private Pane pane = new Pane();

    public static void main(String[] args)
    {
	    Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        //draw the board
        this.thePhrase = getString();
        drawPhrase();

        //set the dementions of the stage
        primaryStage.setTitle("Wheel of Fortune");
        primaryStage.setResizable(false);
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);
        Scene scene = new Scene(this.pane);

        //fluff messages
        Text welcomeMsg = new Text("Welcome to the Wheel of Fortune!");welcomeMsg.setY(25);welcomeMsg.setX(15);welcomeMsg.setUnderline(true);welcomeMsg.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
        Text respect = new Text("Created by Connor Meads");respect.setX(15);respect.setY(45);respect.setFont(Font.font("Brandon Grotesque", 12));

        //playerScore
        Text playerScoreHelpText = new Text("Score:");playerScoreHelpText.setX(primaryStage.getWidth() - 140);playerScoreHelpText.setY(primaryStage.getHeight() - 665);
        Text playerScore = new Text("0");playerScore.setX(primaryStage.getWidth() - 100);playerScore.setY(primaryStage.getHeight() - 665);

        //helpText
        Text helpText = new Text("After you spin the wheel, enter a phrase and punch the button!");helpText.setX(primaryStage.getWidth() - 980);helpText.setY(primaryStage.getHeight() - 130);helpText.setFont(Font.font("Times New Roman", 15));
        Text incorrectGuess = new Text();incorrectGuess.setText("");

        //makeGuess textBox
        TextField guess = new TextField();guess.setPromptText("Enter Letter Here");guess.setLayoutX(primaryStage.getWidth() - 980);guess.setLayoutY(primaryStage.getHeight() - 210);guess.setDisable(true);

        //button declaration
        Button makeGuess = new Button("Punch it");makeGuess.setLayoutX(primaryStage.getWidth() - 980);makeGuess.setLayoutY(primaryStage.getHeight() - 170);makeGuess.setDefaultButton(true);makeGuess.setDisable(true);
        Button exitProgram = new Button("EXIT");exitProgram.setLayoutY(primaryStage.getHeight() - 100);exitProgram.setLayoutX(primaryStage.getWidth() - 150);exitProgram.setMinWidth(120);exitProgram.setMinHeight(60);
        Button spinButton = new Button ("Spin");spinButton.setLayoutY(primaryStage.getHeight() - 300);spinButton.setLayoutX(primaryStage.getWidth() - 890);spinButton.setMinWidth(80);spinButton.setMinHeight(30);
        Button reset = new Button("Reset");reset.setLayoutY(primaryStage.getHeight() - 100);reset.setLayoutX(primaryStage.getWidth() - 980);reset.setMinWidth(100);reset.setMinHeight(50);
        Button customPhrase = new Button("Set Custom Phrase"); customPhrase.setLayoutY(makeGuess.getLayoutY());customPhrase.setLayoutX(makeGuess.getLayoutX()+75);

        //triangle
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(new Double[]{
             220.0, 130.0,
             230.0, 110.0,
             240.0, 120.0});

        //exit Button logic
        exitProgram.setOnAction(event -> {
            System.exit(0);
        });

        //make circle object
        drawWheel();

        //spin button logic
        spinButton.setOnAction(action -> {
            int min = 0;
            int max = 7;
            int range = max - min;

            for(int i = 0; i < 7 + (int)(Math.random() * range); i++)
            {
                for(int j = 0; j <= 7; j++)
                {
                    if(j == 7)
                    {
                        this.scoreValues.get(j).setX(this.scoreValuesXY.get(0).x);
                        this.scoreValues.get(j).setY(this.scoreValuesXY.get(0).y);
                    }
                    else
                    {
                        this.scoreValues.get(j).setX(this.scoreValuesXY.get(j+1).x);
                        this.scoreValues.get(j).setY(this.scoreValuesXY.get(j+1).y);
                    }
                }
                Text temp = scoreValues.get(7);
                scoreValues.remove(7);
                scoreValues.add(0, temp);
//                try{                              ****MY ATTEMPT TO CREATE AN ANIMATION.  IT DIDN'T WORK, CLEARLY.
//                    Thread.sleep(1000);
//                }catch (Exception e)
//                {
//                    System.out.println("ERROR: " + e);
//                }

            }
            makeGuess.setDisable(false);
            guess.setDisable(false);
            spinButton.setDisable(true);
        });

        //makeGuess Button
        makeGuess.setOnAction(action -> {
            helpText.setText("Good guess! Enter In another letter!");
            if(guess.getText().length() == 0)
            {
                helpText.setText("Enter something in before pressing the button");
            }
            else
            {
                //if the guess has any spaces, remove them
                guess.setText(guess.getText().replaceAll(" ", ""));

                if(guessString(guess.getText(), thePhrase)) //instance of a correct guess
                {
                    int previousScore = Integer.parseInt(playerScore.getText());
                    String temp;
                    for(int i = 0; i < lettersInPhrase.size(); i++)//walks through the phrase letter for letter
                    {
                        temp = "";
                        if (lettersInPhrase.get(i).getText().equals(String.valueOf(guess.getText().charAt(0))))//checks if current letter in the phrase equals the first letter in guess
                        {
                            for(int j = 0; j < guess.getText().length(); j++) //makes a temp String that equals the next guess.length() variables in lettersInPhrase
                            {
                                if(!String.valueOf(guess.getText().charAt(j)).equals(lettersInPhrase.get(i + j).getText())) //second check to see if the current letter equals the corresponding letter in guess
                                {
                                    break;
                                }
                                temp += lettersInPhrase.get(i + j ).getText();
                            }

                            if(guess.getText().equals(temp))
                            {
                                for (int j = 0; j < guess.getText().length(); j++)
                                {
                                    if(!lettersInPhrase.get(i + j ).isVisible())
                                    {
                                        previousScore += getPoints();
                                        playerScore.setText(String.valueOf(previousScore));
                                        helpText.setText("Correct!");
                                        lettersInPhrase.get(i + j).setVisible(true);
                                        victoryIncrement++;
                                    }
                                    else
                                    {
                                        helpText.setText("You already guessed that!");
                                    }
                                }
                            }
                        }
                    }

                    if(victoryIncrement == lettersInPhrase.size()) //win condition
                    {
                        helpText.setText("VICTORY!");
                        helpText.setFont(Font.font("Times New Roman",30));
                    }
                }
                else //incorrect guess
                {
                    for (int i = 0; i < guess.getText().length(); i ++)
                    {
                        this.wrongGuesses += String.valueOf(guess.getText().charAt(i)); //adds each letter of wrong letter
                        numWrongGuesses++;//increment the number of guesses every time a wrong answer is guessed
                    }

                    this.wrongGuesses += " ";

                    //set the specifications for the incorrectGuess Text field
                    incorrectGuess.setText(this.wrongGuesses); incorrectGuess.setX(primaryStage.getWidth() - 980); incorrectGuess.setY(primaryStage.getHeight() - 240); incorrectGuess.setFont(Font.font("Times New Roman", FontWeight.BOLD, 30));

                    playerScore.setText("0");
//                    if(numWrongGuesses >= 6) //player loses
//                    {
//                        helpText.setText("GAME OVER, YOU LOSE");
//
//                        for (int i = 0; i < lettersInPhrase.size(); i++) //set the remaining letters to be visible
//                        {
//                            lettersInPhrase.get(i).setVisible(true);
//                        }
//                    }
                }
            }
            guess.clear();
            spinButton.setDisable(false);
            guess.setDisable(true);
            makeGuess.setDisable(true);
        });

        //reset button
        reset.setOnAction(action -> {
            //reset EVERYTHING back to default values

            if(this.inReset)
            {
                this.thePhrase = getString();
            }
            else
            {
                this.thePhrase = guess.getText();
            }
            this.inReset = true;
            incorrectGuess.setText(""); //reset incorrect guess line
            this.victoryIncrement = 0; //reset the victoryIncrement
            numWrongGuesses = 0;
            wrongGuesses = "";
            helpText.setText("");
            helpText.setFont(Font.font("Times New Roman",15));
            deletePhrase(); //delete the current board
            drawPhrase(); //draw the phrase and bases again
            deleteWheel();
            drawWheel();
            spinButton.setDisable(false);
            guess.setDisable(true);
            makeGuess.setDisable(true);

        });

        //custom phrase button
        customPhrase.setOnAction(action -> {
            this.inReset = false;
            reset.fire();
            guess.clear();
        });

        //add everything to the stage
        this.pane.getChildren().addAll(respect, welcomeMsg, helpText, playerScore, playerScoreHelpText, exitProgram, spinButton, reset, customPhrase, makeGuess, incorrectGuess, guess, triangle);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void deleteWheel()
    {
        this.pane.getChildren().remove(wheel);
        for (int i = 0; i < this.scoreValues.size(); i++)
        {
            this.pane.getChildren().remove(this.scoreValues.get(i));
        }
        this.scoreValues.clear();
    }

    private void drawWheel()
    {
        //variables
        ObservableList<Line> dividers = FXCollections.observableArrayList();
        this.wheel.setCenterY(250);this.wheel.setCenterX(150);this.wheel.setRadius(125);this.wheel.setStroke(Color.BLACK);this.wheel.setFill(Color.WHITESMOKE);
        int [] l_potentialPoints = {100, 500, 400, 600, 200, 800, 300, 700,};
        //x,y coordinate variables
        double x = wheel.getCenterX();
        double y = wheel.getCenterY();
        double angle = 0;
        double x1, x2, y1, y2;
        //math.random variables
        int min = 0;
        int max = l_potentialPoints.length;
        int range = max - min;

        for(int i = 0; i < l_potentialPoints.length; i++) //set random values as the scores
        {
            Text newText = new Text(Integer.toString(l_potentialPoints[((int)(Math.random() * range))]));
            newText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 15));
            this.scoreValues.add(newText);
        }

        for(int i = 0; i < this.scoreValues.size(); i++)//set the x's and y's for the scoreValues
        {
            Point tempPoint = new Point();
            this.scoreValues.get(i).setX((int)x + (wheel.getRadius() / 1.25) * Math.cos((angle + (45/2)) * (Math.PI / 180)));
            this.scoreValues.get(i).setY((int)y + (wheel.getRadius() / 1.25) * Math.sin((angle + (45/2)) * (Math.PI / 180)));
            tempPoint.x = (int)this.scoreValues.get(i).getX();
            tempPoint.y = (int)this.scoreValues.get(i).getY();

            scoreValuesXY.add(tempPoint);//add that point to an List of points
            x -= 2;
            y += 1;
            angle += 45;
        }

        //make dividing lines & add potential Scores
        angle = 0; //reset angle
        y = 250;
        x = 150;
        for(int i = 0; i < 4; i++)
        {
            //set the beginning x's (x1) & y's (y1) as well as ending x's (x2) and ending y's (y2) for circle
            x1 = x + wheel.getRadius() * Math.cos(angle * (Math.PI / 180));
            x2 = x + wheel.getRadius() * Math.cos((angle + 180) * Math.PI / 180);
            y1 = y + wheel.getRadius() * Math.sin(angle * (Math.PI / 180));
            y2 = y + wheel.getRadius() * Math.sin((angle + 180) * (Math.PI / 180));

            Line newLine = new Line(x1, y1, x2, y2);
            newLine.setFill(Color.BLACK);

            dividers.add(newLine);
            angle += 45;
        }
        this.pane.getChildren().add(this.wheel); //add the wheel to the pane
        //add lines to the pane
        for(int i = 0; i < dividers.size(); i++)
        {
            this.pane.getChildren().add(dividers.get(i));
        }
        for(int i = 0; i < this.scoreValues.size(); i++) //add scoreValues to the pane
        {
            this.pane.getChildren().add(this.scoreValues.get(i));
        }
    }

    private int getPoints()
    {
        double x = 175.0;
        double y =163.0;

        for(int i = 0; i < scoreValues.size(); i++)
        {
            if(scoreValues.get(i).getX() == x && scoreValues.get(i).getY() == y)
            {
                return Integer.parseInt(scoreValues.get(i).getText());
            }
        }
        return 0;
    }

    private void drawPhrase()
    {
        //WORD BASES

        //initial x & y values
        int x = 430;
        int y = 110;
        int wordBaseWidth = 60;
        for(int i = 0; i < thePhrase.length(); i++)
        {
            if(thePhrase.charAt(i) == ' ') //if the next character is a space, return to next line
            {
                x = 430;
                y += 75;
            }
            else
            {
                Line newLine = new Line(x, y, x + wordBaseWidth / 2, y);
                wordBase.add(newLine);

                x += wordBaseWidth;
            }
        }

        //reset x & y
        x = 425;
        y= 110;

        for(int i = 0; i < thePhrase.length(); i++)
        {
            if(thePhrase.charAt(i) == ' ') //if the character is a ' ', set it to a new line
            {
                x = 425;
                y += 75;
            }
            else
            {
                Text newLetter = new Text(x + wordBaseWidth / 4, y-5, String.valueOf(thePhrase.charAt(i)));
                newLetter.setFont(Font.font("Times New Roman", FontWeight.BOLD, 30));
                newLetter.setVisible(false);
                lettersInPhrase.add(newLetter);
                x += wordBaseWidth;
            }
        }

        for(int i = 0; i < wordBase.size(); i++)
        {
            this.pane.getChildren().add(wordBase.get(i));
            this.pane.getChildren().add(lettersInPhrase.get(i));
        }
    }

    private void deletePhrase()
    {
        for (int i = 0; i < this.wordBase.size(); i++)
        {
            this.pane.getChildren().remove(this.wordBase.get(i));
            this.pane.getChildren().remove(this.lettersInPhrase.get(i));
        }
        wordBase.clear();
        lettersInPhrase.clear();
    }

    private boolean guessString(String guess, String l_phrase)
    {
        int increment = 0;

        l_phrase = l_phrase.replaceAll(" ", ""); //remove all spaces out of l_phrase

        if(guess.length() <= l_phrase.length())
        {
            for (int i = 0; i < l_phrase.length(); i++) //goes through the entire phrase
            {
                if (guess.charAt(0) == l_phrase.charAt(i)) //checks to see if the char we're at equals the first char of our guess
                {
                    for (int j = 0; j < guess.length(); j++) //goes through the guess
                    {
                        if (l_phrase.length() <= i +j)
                        {
                            break;
                        }
                        if (guess.charAt(j) == l_phrase.charAt(i + j)) //compares each letter
                        {
                            increment++;
                            if (increment == guess.length()) //the two words match, it's true
                            {
                                return true;
                            }
                        }
                    }
                }
                increment = 0; //reset increment
            }
        }
        return false;
    }

    private String getString()
    {
        String contents;
        int min= 0;
        int max;
        int range = 0;

        try
        {
            //declare the file & bufferedReader
            FileReader fr = new FileReader("C:\\Users\\conno\\IdeaProjects\\hw_3B_cs2410\\src\\com\\company\\phrases.txt");
            BufferedReader br = new BufferedReader(fr);

            for(int i = 0; (contents = br.readLine()) != null; i++) //adds the entire .txt file into a List
            {
                this.fileContents.add(contents.toLowerCase());
            }

            max = this.fileContents.size();

            range = max - min;

            br.close();
        }catch(Exception e)
        {
            System.out.println("Error: " + e);
        }
        return fileContents.get((int)(Math.random() * range));

    }
}
