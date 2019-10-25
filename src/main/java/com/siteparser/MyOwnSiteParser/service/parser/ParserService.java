package com.siteparser.MyOwnSiteParser.service.parser;

import com.siteparser.MyOwnSiteParser.domain.Page;
import com.siteparser.MyOwnSiteParser.domain.Project;
import com.siteparser.MyOwnSiteParser.service.jpa.PageService;
import com.siteparser.MyOwnSiteParser.service.jpa.ProjectService;
import com.siteparser.MyOwnSiteParser.service.parse.CleanTextService;
import com.siteparser.MyOwnSiteParser.service.parse.ExtractLinksService;
import com.siteparser.MyOwnSiteParser.service.parse.HtmlLoadService;
import com.siteparser.MyOwnSiteParser.service.parse.MetadataService;
import com.siteparser.MyOwnSiteParser.service.parse.stats.HeadlinesLengthService;
import com.siteparser.MyOwnSiteParser.service.parse.stats.ParagraphLengthService;
import com.siteparser.MyOwnSiteParser.service.search.SearchService;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ParserService {

    @Autowired
    private HtmlLoadService htmlLoadService;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private HeadlinesLengthService headlinesLengthService;

    @Autowired
    private ParagraphLengthService paragraphLengthService;

    @Autowired
    private CleanTextService cleanTextService;

    @Autowired
    private PageService pageService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ExtractLinksService extractLinksService;

    @Autowired
    private SearchService searchService;

    @Scheduled(fixedDelay = 1000)
    public void parse() {
        List<Project> projectsToParse = projectService.getAllWithEnabledParsing();
        for (Project project : projectsToParse) {
            if (pageService.hasUnparsedProjectPages(project)) {
                parseProject(project);
                break;
            }else{
                project.setParsingStatus(false);
                projectService.saveProject(project);
            }
        }
    }

    private void parseProject(Project project) {
        List<Page> pagesToParse = pageService.getUnparsedProjectPages(project);
        projectService.saveProject(project);
        Page firstPage = pagesToParse.get(0);
        parsePage(firstPage, project);
    }


    private void parsePage(Page firstPage, Project project) {
        String url = firstPage.getUrl();
        Document document = null;
        try {
            document = htmlLoadService.loadDocument(url, project);
            if (document != null) {
                String title = cleanTextService.clean(metadataService.getTitle(document));
                String description = cleanTextService.clean(metadataService.getDescription(document));
                String content = cleanTextService.clean(document.body().text());
                firstPage.setTitle(title);
                firstPage.setDescription(description);
                firstPage.setContent(content);
                pageService.save(firstPage);
                List<String> extractedLinks = extractLinksService.extract(firstPage.getProject(), document);
                for (String link : extractedLinks) {
                    Page page = new Page();
                    page.setUrl(link);
                    page.setProject(firstPage.getProject());
                    pageService.save(page);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
