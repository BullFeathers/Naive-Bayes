import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */

public class NaiveBayesClassifierImpl implements NaiveBayesClassifier {

	//THESE VARIABLES ARE OPTIONAL TO USE, but HashMaps will make your life much, much easier on this assignment.

	//dictionaries of form word:frequency that store the number of times word w has been seen in documents of type label
	//for example, comedyCounts["mirth"] should store the total number of "mirth" tokens that appear in comedy documents
   private HashMap<String, Integer> tragedyCounts = new HashMap<String, Integer>();
   private HashMap<String, Integer> comedyCounts = new HashMap<String, Integer>();
   private HashMap<String, Integer> historyCounts = new HashMap<String, Integer>();
   
   //prior probabilities, ie. P(T), P(C), and P(H)
   //use the training set for the numerator and denominator
   private double tragedyPrior;
   private double comedyPrior;
   private double historyPrior; 
   
   private double tDocumentSum;
   private double cDocumentSum;
   private double hDocumentSum;
   
   //total number of word TOKENS for each type of document in the training set, ie. the sum of the length of all documents with a given label
   private int tTokenSum;
   private int cTokenSum;
   private int hTokenSum;
   
   //full vocabulary, update in training, cardinality is necessary for smoothing
   private HashSet<String> vocabulary = new HashSet<String>();
   

  /**
   * Trains the classifier with the provided training data
   Should iterate through the training instances, and, for each word in the documents, update the variables above appropriately.
   The dictionary of frequencies and prior probabilities can then be used at classification time.
   */
  @Override
  public void train(Instance[] trainingData) {
	  for (int i = 0; i < trainingData.length; i++) {
		  tallyLabel(trainingData[i]);
		  for (int j = 0; j < trainingData[i].words.length; j++) {
			  //if (!trainingData[i].words[j].equals("")) {
				  tallyWord(trainingData[i].label, trainingData[i].words[j]);
			  //}
		  }
	  }
  }
  
  /**
   * This function increments the corresponding document tally.
   * @param instance
   */
  private void tallyLabel(Instance instance) {
	  switch(instance.label) {
		  case COMEDY:
			  cDocumentSum+=1.0;
			  break;
		  case HISTORY:
			  hDocumentSum+=1.0;
			  break;
		  case TRAGEDY:
			  tDocumentSum+=1.0;
			  break;
		  default:
	  }
  }
  
  /**
   * With the given word, this function updates the appropriate count hashmap and increments the appropriate token sum.
   * @param genre
   * @param word
   */
  private void tallyWord(Label genre, String word) {
	  if (!vocabulary.contains(word)) {
		  vocabulary.add(word);
	  }
	  
	  switch(genre) {
		  case COMEDY:
			  if (!comedyCounts.containsKey(word)) {
				  comedyCounts.put(word, 1);
			  } else {
				  comedyCounts.put(word, comedyCounts.get(word) + 1);
			  }
			  cTokenSum++;
			  break;
		  case HISTORY:
			  if (!historyCounts.containsKey(word)) {
				  historyCounts.put(word, 1);
			  } else {
				  historyCounts.put(word, historyCounts.get(word) + 1);
			  }
			  hTokenSum++;
			  break;
		  case TRAGEDY:
			  if (!tragedyCounts.containsKey(word)) {
				  tragedyCounts.put(word, 1);
			  } else {
				  tragedyCounts.put(word, tragedyCounts.get(word) + 1);
			  }
			  tTokenSum++;
			  break;
		  default:
			  
	  }
  }

  /*
   * Prints out the number of documents for each label
   * A sanity check method
   */
  public void documents_per_label_count(){
	  System.out.println("Documents per Comedy label count: " + cDocumentSum);
	  System.out.println("Documents per History label count: " + hDocumentSum);
	  System.out.println("Documents per Tragedy label count: " + tDocumentSum);
  }

  /*
   * Prints out the number of words for each label
	Another sanity check method
   */
  public void words_per_label_count(){
    // TODO : Implement
	  System.out.println("Words per Comedy label count: " + cTokenSum);
	  System.out.println("Words per History label count: " + hTokenSum);
	  System.out.println("Words per Tragedy label count: " + tTokenSum);
  }

  /**
   * Returns the prior probability of the label parameter, i.e. P(COMEDY) or P(TRAGEDY)
   */
  @Override
  public double p_l(Label label) {
	  switch(label) {
		  case COMEDY:
			  comedyPrior = cDocumentSum/(cDocumentSum + hDocumentSum + tDocumentSum);
			  return comedyPrior;
		  case HISTORY:
			  historyPrior = hDocumentSum/(cDocumentSum + hDocumentSum + tDocumentSum);
			  return historyPrior;
		  case TRAGEDY:
			  tragedyPrior = tDocumentSum/(cDocumentSum + hDocumentSum + tDocumentSum);
			  return tragedyPrior;
		  default:
			  return 0;
	  }
  }
  
  /**
   * Returns the sum of the occurrences of the label parameter
   * @param label
   * @return
   */
  public double labelSum(Label label) {
	  switch(label) {
		  case COMEDY:
			  return cDocumentSum;
		  case HISTORY:
			  return hDocumentSum;
		  case TRAGEDY:
			  return tDocumentSum;
		  default:
			  return 0;
	  }
  }  
  
  /**
   * Returns the sum of the occurrences of the word in the genre.
   * @param label
   * @return
   */
  public double wordSum(Label label) {
	  switch(label) {
		  case COMEDY:
			  return cTokenSum;
		  case HISTORY:
			  return hTokenSum;
		  case TRAGEDY:
			  return tTokenSum;
		  default:
			  return 0;
	  }
  }

  /**
   * Returns the smoothed conditional probability of the word given the label, i.e. P(word|COMEDY) or
   * P(word|HISTORY)
   */
  @Override
  public double p_w_given_l(String word, Label label) {
    double num = 0.0;
    double den = 1.0;
    
    num = labelSum (label) + 0.00001;
    den = 0.00001 * vocabulary.size() + wordSum(label);
    
    return num/den;
  }

  /**
   * Classifies a document as either a Comedy, History, or Tragedy.
   Break ties in favor of labels with higher prior probabilities.
   */
  @Override
  public Label classify(Instance ins) {
  
	// TODO : Implement
	
	//Initialize sum probabilities for each label
	  
	//For each word w in document ins
	  for (int j = 0; j < ins.words.length; j++) {
		  if (!ins.words[j].equals("")) {
			  //tallyWord(ins.label, ins.words[j]);
		  }
	  }
		//compute the log (base e or default java log) probability of w|label for all labels (COMEDY, TRAGEDY, HISTORY)
		//add to appropriate sum
	//Return the Label of the maximal sum probability

    return null; 
  }
  
  
  
  
}
