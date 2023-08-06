import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class showyourquestionView extends JFrame implements ActionListener {

    Container container = getContentPane();
    JLabel label = new JLabel("STACKOVERLOW");
    Question question;
    JLabel Label_title = new JLabel("VIEW QUESTION");
    JLabel title = new JLabel("Title");
    JLabel descriptionLabel = new JLabel("Description");
    JLabel titleField;
    JTextArea descriptionField;
    JLabel name = new JLabel("Question posted by");
    JLabel nameField;
    JLabel vote = new JLabel("Votes");
    JLabel voteField;
    conn connection;
    JButton AddAnswerButton = new JButton("Add Answer");
    JButton ViewAnswers = new JButton("View Answers");
    JButton ViewComments = new JButton("View Comments");
    JButton AddCommentButton = new JButton("Add Comments");
    JButton backButton = new JButton("BACK");
    JButton logoutButton = new JButton("LOGOUT");
    JButton upvote = new JButton("UPVOTE");
    JButton downvote = new JButton("DOWNVOTE");
    JScrollPane scroll;
    Member m;

    showyourquestionView(Question question, conn c1) {

        this.question = question;
        this.connection = c1;
        this.m = null;
        try{
          m = this.getMember();
        }
        catch(Exception err){
          System.err.println(err);
        }
        try{
          getcomment();
          getanswer();
        }
        catch(Exception err){
          System.err.println(err);
        }
        titleField = new JLabel(this.question.title);
        descriptionField = new JTextArea(this.question.description);
        scroll = new JScrollPane (descriptionField, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        nameField = new JLabel(m.name);
        voteField = new JLabel(Integer.toString(this.question.voteCount));
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        addActionEvent();

        setTitle("Write your question");
        setVisible(true);
        setBounds(10, 10, 1100, 1000);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setResizable(true);
        descriptionField.setEditable(false);
        descriptionField.setVisible(true);
    }

    public void setLayoutManager() {
        container.setLayout(null);
    }

    public void setLocationAndSize() {
        label.setFont(new Font("Serif", Font.BOLD, 28));
        label.setForeground(new java.awt.Color(255,165,0));
        Label_title.setFont(new Font("Serif", Font.BOLD, 28));
        Label_title.setForeground(new java.awt.Color(41,86,143));
        name.setFont(new Font("Serif", Font.BOLD, 24));
        title.setFont(new Font("Serif", Font.BOLD, 22));
        nameField.setFont(new Font("Serif", Font.BOLD, 22));
        titleField.setFont(new Font("Serif", Font.BOLD, 22));
        vote.setFont(new Font("Serif", Font.BOLD, 22));
        voteField.setFont(new Font("Serif", Font.BOLD, 22));
        descriptionLabel.setFont(new Font("Serif", Font.BOLD, 22));

        label.setBounds(350,30,500,100);
        logoutButton.setBounds(900, 10, 150, 30);
        Label_title.setBounds(370,120,500,50);
        name.setBounds(350,200,500,30);
        title.setBounds(350, 250, 100, 30);
        vote.setBounds(350, 300, 100, 30);
        descriptionLabel.setBounds(400, 350, 700, 30);
        nameField.setBounds(550, 200, 500, 30);
        titleField.setBounds(450,250, 750, 30);
        voteField.setBounds(450, 300, 750, 30);
        scroll.setBounds(250, 400, 550, 400);
        AddAnswerButton.setBounds(50, 500, 150, 30);
        AddCommentButton.setBounds(50, 600, 150, 30);
        ViewAnswers.setBounds(50,10, 150, 30);
        ViewComments.setBounds(50, 60, 150, 30);
        backButton.setBounds(900,60,150,30);
        upvote.setBounds(900,500,150,30);
        downvote.setBounds(900, 600, 150, 30);
    }

    public void addComponentsToContainer() {
        container.add(label);
        container.add(Label_title);
        container.add(title);
        container.add(descriptionLabel);
        container.add(titleField);
        container.add(AddAnswerButton);
        container.add(scroll);
        container.add(AddCommentButton);
        container.add(ViewAnswers);
        container.add(ViewComments);
        container.add(backButton);
        container.add(name);
        container.add(nameField);
        container.add(vote);
        container.add(voteField);
        container.add(upvote);
        container.add(downvote);
        container.add(logoutButton);
    }

    public void addActionEvent() {
      AddAnswerButton.addActionListener(this);
      AddCommentButton.addActionListener(this);
      ViewAnswers.addActionListener(this);
      ViewComments.addActionListener(this);
      backButton.addActionListener(this);
      upvote.addActionListener(this);
      downvote.addActionListener(this);
      logoutButton.addActionListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if(e.getSource()== AddAnswerButton){
        new addanswers(question, m, connection);
      }
      if(e.getSource()==AddCommentButton){
        new addcomments(question, m, connection);
      }
      if(e.getSource()==ViewAnswers){
        new viewanswers(question, m, connection);
      }
      if(e.getSource()==ViewComments){
        new viewcomments(question, m, connection);
      }
      if(e.getSource()==backButton){
        setVisible(false);
        new homeView(m, connection);
      }
      if(e.getSource()==upvote){
        try{
          this.question.incrementVoteCount();
          updatequestionVote(question,1);
          upvote.setEnabled(false);
          downvote.setEnabled(true);
          this.dispose();
          new  showyourquestionView(question, connection);
        }
        catch(Exception err){
          System.err.println(err);
        }
      }
      if(e.getSource()==downvote){
        try{
          this.question.decrementVoteCount();
          updatequestionVote(question, 0);
          upvote.setEnabled(true);
          downvote.setEnabled(false);
          this.dispose();
          new showyourquestionView(question, connection);
        }
        catch(Exception err){
          System.err.println(err);
        }
      }
    }

    public void updatequestionVote(Question q, int voteType) throws Exception {
     String query = "UPDATE question SET voteCount=" + q.voteCount + " WHERE questionid='" + q.questionid + "';";
      try {
          Class.forName("org.postgresql.Driver");
      }
      catch (ClassNotFoundException e) {
          System.err.println (e);
          System.exit (-1);
      }
      try {
          connection.c.setAutoCommit(false);
          Statement statement = connection.createStatement ();
          statement.executeUpdate(query);

          statement.close();
          connection.c.commit();
          
      }
      catch(Exception e){
          throw e;
      }

    }

    public void getanswer() throws Exception {
      String query = "SELECT * FROM Answers WHERE questionid=\'"+question.questionid+"\';";
      System.out.println(query);
      try {
          Class.forName("org.postgresql.Driver");
      }
      catch (ClassNotFoundException e) {
          System.err.println (e);
          System.exit (-1);
      }
      try {
          Statement statement = connection.createStatement ();
          ResultSet rs = statement.executeQuery(query);
          while(rs.next()){
            this.question.addAnswer(new Answer(rs.getString("answer_text"), rs.getInt("votecount")));
          }
          rs.close();
          statement.close();
      }
      catch(Exception e){
          throw e;
      }

    }

    public void getcomment() throws Exception {
      String query = "SELECT * FROM Comments WHERE questionid=\'"+question.questionid+"\';";
      System.out.println(query);
      try {
          Class.forName("org.postgresql.Driver");
      }
      catch (ClassNotFoundException e) {
          System.err.println (e);
          System.exit (-1);
      }
      try {
          Statement statement = connection.createStatement ();
          ResultSet rs = statement.executeQuery(query);
          while(rs.next()){
            this.question.addComment(new Comment(rs.getString("text"), rs.getInt("votecount")));
          }
          rs.close();
          statement.close();
      }
      catch(Exception e){
          throw e;
      }

    }

    public Member getMember() throws Exception{
      String query = "SELECT * FROM users WHERE memid=\'"+question.memid+"\';";
      System.out.println(query);
      try {
          Class.forName("org.postgresql.Driver");
      }
      catch (ClassNotFoundException e) {
          System.err.println (e);
          System.exit (-1);
      }
      try {
          Statement statement = connection.createStatement ();
          ResultSet set = statement.executeQuery(query);
          if(set.next()){
            Member m = new Member(set.getString("name"), set.getString("password"), set.getString("email"), set.getString("phone"), set.getString("memid"), 
            set.getBoolean("isModerator"), set.getBoolean("isAdmin"), set.getBoolean("acc_blocked"), set.getInt("reputation"));
          set.close();
          statement.close();
          return m;
          }
          else{
            set.close();
            statement.close();
            return null;
          }
      }
      catch(Exception e){
        throw e;
      }
    }
}
