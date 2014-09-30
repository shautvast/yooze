package yooze.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import yooze.ClassCache;
import yooze.ClassModelBuilder;
import yooze.GraphBuilder;
import yooze.InclusionDecider;
import yooze.domain.ClassModel;
import yooze.domain.Graph;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@Controller
public class YoozeServer {
	private Graph raw;
	private ClassCache classCache;

	@RequestMapping("/graph")
	@ResponseBody
	public NodesAndLinks nodesAndLinks() {
		ConcurrentHashMap<ClassModel, Integer> classes = new ConcurrentHashMap<ClassModel, Integer>();
		int i = 0;
		List<Node> nodes = new ArrayList<Node>();
		List<Link> links = new ArrayList<Link>();
		for (ClassModel classModel : classCache.values()) {
			classes.put(classModel, i++);
			nodes.add(new Node(classModel.getName().substring(classModel.getName().lastIndexOf(".") + 1)));

		}
		for (ClassModel classModel : classCache.values()) {
			for (ClassModel ref : classModel.getReferences()) {
				links.add(new Link(classes.get(classModel), classes.get(ref)));
			}
		}
		return new NodesAndLinks(nodes, links);
	}

	@RequestMapping("/raw")
	@ResponseBody
	public Graph raw() {
		return raw;
	}

	static class NodesAndLinks {
		private List<Node> nodes;
		private List<Link> links;

		public NodesAndLinks(List<Node> nodes, List<Link> links) {
			super();
			this.nodes = nodes;
			this.links = links;
		}

		public List<Link> getLinks() {
			return links;
		}

		public List<Node> getNodes() {
			return nodes;
		}

	}

	static class Node {
		String name;

		public Node(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	static class Link {
		private int source, target;

		public Link(int source, int target) {
			super();
			this.source = source;
			this.target = target;
		}

		public int getSource() {
			return source;
		}

		public int getTarget() {
			return target;
		}
	}

	@PostConstruct
	public void startup() throws IOException {
		InclusionDecider i = new InclusionDecider();
		i.setPackageExcludePatterns("java\\.", ".*Regelkosten", "org\\.", "dao", "\\$");
		i.setPackageIncludePatterns("Service$", "ServiceImpl$", "ServiceBean$", "Handelingen$");
		GraphBuilder directoryBuilder = GraphBuilderFactory.getJarBuilder();
		classCache = new ClassCache();
		classCache.setInclusionDecider(i);
		ClassModelBuilder classModelBuilder = new ClassModelBuilder();
		classModelBuilder.setClassCache(classCache);
		directoryBuilder.setClassModelBuilder(classModelBuilder);

		classModelBuilder.setInclusionDecider(i);
		raw = directoryBuilder.build("src/test/resources/trc-common-core-1.0.jar", null);

	}

	public static void main(String[] args) {
		SpringApplication.run(YoozeServer.class, args);
	}
}
