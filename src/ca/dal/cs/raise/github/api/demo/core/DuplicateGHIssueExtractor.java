package ca.dal.cs.raise.github.api.demo.core;

import java.util.ArrayList;
import java.util.Collection;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import ca.dal.cs.raise.github.api.demo.config.StaticData;

public class DuplicateGHIssueExtractor {

	String repoName;
	String companyName;
	ArrayList<Integer> issueNumbers;

	public DuplicateGHIssueExtractor(String companyName, String repoName, ArrayList<Integer> issueNumbers) {
		this.repoName = repoName;
		this.companyName = companyName;
		this.issueNumbers = issueNumbers;
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

	protected boolean isDuplicate(Collection<GHLabel> labels) {
		for (GHLabel label : labels) {
			if (label.getName().contains("Duplicate") || label.getName().contains("duplicate")) {
				return true;
			}
		}
		return false;
	}

	protected ArrayList<Integer> detectDuplicateIssue() {
		ArrayList<Integer> duplicates = new ArrayList<>();
		try {
			GHRepository repository = getGHRepository();
			for (int issueNumber : this.issueNumbers) {
				GHIssue issue = repository.getIssue(issueNumber);
				Collection<GHLabel> collection = issue.getLabels();
				if (isDuplicate(collection)) {
					duplicates.add(issueNumber);
				}
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return duplicates;
	}

	public static void main(String[] args) {
		String repoName = "pandas";
		String companyName = "pandas-dev";
		ArrayList<Integer> issues = new ArrayList<>();
		issues.add(42604);
		issues.add(40257);
		issues.add(45253);

		DuplicateGHIssueExtractor dupe = new DuplicateGHIssueExtractor(companyName, repoName, issues);
		System.out.println(dupe.detectDuplicateIssue());

	}

}
