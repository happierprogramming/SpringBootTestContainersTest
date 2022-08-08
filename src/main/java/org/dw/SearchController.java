package org.dw;

import org.apache.http.HttpHost;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.get.GetResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
public class SearchController {

    private final RestHighLevelClient client;

    public SearchController(@Value("${opensearch.host}") String host,
                            @Value("${opensearch.port}") int port) {
        this.client = new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, "http")));
    }

    @PutMapping("/insert/{id}")
    public void insert(@PathVariable("id") String id,
                       @RequestParam("name") String name,
                       @RequestParam("age") String age) throws IOException {
        client.index(new IndexRequest()
                        .id(id)
                        .index("index")
                        .source("name", name, "age", age),
                RequestOptions.DEFAULT);
    }

    @GetMapping("/search/{id}")
    public Map<String, Object> search(@PathVariable("id") String id) throws IOException {
        GetRequest getRequest = new GetRequest("index", id);
        GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
        return response.getSource();
    }
}
