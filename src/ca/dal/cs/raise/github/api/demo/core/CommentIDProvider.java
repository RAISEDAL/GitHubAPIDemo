package ca.dal.cs.raise.github.api.demo.core;

import java.util.HashMap;
import org.kohsuke.github.GHIssueComment;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestReviewComment;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterator;
import ca.dal.cs.raise.github.api.demo.config.StaticData;

public class CommentIDProvider {

	String repoName;
	String companyName;
	int prNumber;
	String commentText;
	HashMap<Long, String> commentAuthorMap;

	public CommentIDProvider(String companyName, String repoName, int prNumber, String commentText) {
		this.companyName = companyName;
		this.repoName = repoName;
		this.prNumber = prNumber;
		this.commentText = commentText;
		this.commentAuthorMap = new HashMap<>();
	}

	protected HashMap<Long, String> getIssueComments(GHPullRequest request) {
		HashMap<Long, String> commentMap = new HashMap<>();
		try {
			PagedIterator<GHIssueComment> commentsIter = request.listComments().iterator();
			while (commentsIter.hasNext()) {
				GHIssueComment comment = commentsIter.next();
				long commentID = comment.getId();
				String commentText = comment.getBody();
				commentMap.put(commentID, commentText);
				String user = comment.getUser().getLogin();
				this.commentAuthorMap.put(commentID, user);
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return commentMap;
	}

	protected HashMap<Long, String> getReviewComments(GHPullRequest request) {
		HashMap<Long, String> commentMap = new HashMap<>();
		try {
			PagedIterator<GHPullRequestReviewComment> commentsIter = request.listReviewComments().iterator();
			while (commentsIter.hasNext()) {
				GHPullRequestReviewComment comment = commentsIter.next();
				long commentID = comment.getId();
				String commentText = comment.getBody();
				commentMap.put(commentID, commentText);
				String user = comment.getUser().getLogin();
				this.commentAuthorMap.put(commentID, user);

			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return commentMap;
	}

	protected GHRepository getGHRepository() {
		try {
			GitHub github = GitHub.connect(StaticData.LOGIN, StaticData.DEVELOPER_ACCESS_TOKEN);
			if (github != null) {
				return github.getRepository(companyName + "/" + repoName);
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return null;
	}

	protected void showComments(HashMap<Long, String> commentMap) {
		for (long key : commentMap.keySet()) {
			String commentText = commentMap.get(key);
			System.out.println(key + "\t" + commentText);
		}
	}

	protected long findTargetComment(HashMap<Long, String> commentMap) {
		for (long key : commentMap.keySet()) {
			String commentText = commentMap.get(key);
			if (commentText.startsWith(this.commentText)) {
				return key;
			}
		}
		return 0;
	}

	public void detectCommentID() {
		try {
			GHRepository repository = getGHRepository();
			GHPullRequest request = repository.getPullRequest(this.prNumber);
			HashMap<Long, String> issueComments = getIssueComments(request);
			// showComments(issueComments);
			long issueCommentID = findTargetComment(issueComments);
			System.out.println("Found:" + issueCommentID);
			if(this.commentAuthorMap.containsKey(issueCommentID)){
				System.out.println(this.commentAuthorMap.get(issueCommentID));
			}
			
			
			HashMap<Long, String> reviewComments = getReviewComments(request);
			// showComments(reviewComments);
			long reviewCommentID = findTargetComment(reviewComments);
			System.out.println("Found:" + reviewCommentID);
			if(this.commentAuthorMap.containsKey(reviewCommentID)){
				System.out.println(this.commentAuthorMap.get(reviewCommentID));
			}
			
			
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String companyName = "meteor";
		String repoName = "meteor";
		int prNumber = 11590;
		String commentText = "It's working fine on MacOS as well";
		CommentIDProvider ciProvider = new CommentIDProvider(companyName, repoName, prNumber, commentText);
		ciProvider.detectCommentID();
	}

}
