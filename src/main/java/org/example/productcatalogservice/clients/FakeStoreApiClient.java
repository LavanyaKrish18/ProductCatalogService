package org.example.productcatalogservice.clients;

import org.example.productcatalogservice.dtos.FakeStoreProductDto;
import org.example.productcatalogservice.models.Product;
import org.example.productcatalogservice.services.ProductConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class FakeStoreApiClient {

    @Autowired
    private RestTemplateBuilder restTemplateBuilder; // RestTemplate -  Http client to make 3rd party api http request

    @Autowired
    private ProductConverter productConverter;

    public FakeStoreProductDto getProductById(Long id) {
        //.class - datatype of response object
        ResponseEntity<FakeStoreProductDto> fakeStoreProductDtoResponseEntity =
                requestForEntity("https://fakestoreapi.com/products/{id}", HttpMethod.GET, null, FakeStoreProductDto.class, id);
        if (fakeStoreProductDtoResponseEntity.getStatusCode().is2xxSuccessful() && fakeStoreProductDtoResponseEntity.getBody() != null) {
            return fakeStoreProductDtoResponseEntity.getBody();
        }
        return null;
    }

    public FakeStoreProductDto[] getAllProducts() {
        //List<FakeStoreProductDto>.class - not possible - check main2.java
        return requestForEntity("https://fakestoreapi.com/products", HttpMethod.GET, null,
                FakeStoreProductDto[].class).getBody();
    }

    public FakeStoreProductDto replaceProduct(Long id, Product product) {
        FakeStoreProductDto input = productConverter.getFakeStoreProductDto(product);
        return requestForEntity("https://fakestoreapi.com/products/{id}", HttpMethod.PUT, input,
                FakeStoreProductDto.class, id).getBody();
    }

    public FakeStoreProductDto createProduct(Product product) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        FakeStoreProductDto input = productConverter.getFakeStoreProductDto(product);
        return restTemplate.postForEntity("https://fakestoreapi.com/products", input,
                FakeStoreProductDto.class).getBody();
//        FakeStoreProductDto input = productConverter.getFakeStoreProductDto(product);
//        return requestForEntity("https://fakestoreapi.com/products", HttpMethod.POST, input,
//                FakeStoreProductDto.class).getBody();
    }

    public FakeStoreProductDto replacePartialProduct(Long id, Product product) {
        FakeStoreProductDto input = productConverter.getFakeStoreProductDto(product);
        return requestForEntity("https://fakestoreapi.com/products/{id}", HttpMethod.PATCH, input,
                FakeStoreProductDto.class, id).getBody();
    }

    public FakeStoreProductDto deleteProduct(Long id) {
        return requestForEntity("https://fakestoreapi.com/products/{id}", HttpMethod.DELETE, null,
                FakeStoreProductDto.class, id).getBody();
    }

    // Separate methods available for get-getForEntity, post-postForEntity in RestTemplate class
    private <T> org.springframework.http.ResponseEntity<T> requestForEntity(java.lang.String url, HttpMethod httpMethod, @org.springframework.lang.Nullable java.lang.Object request, java.lang.Class<T> responseType, java.lang.Object... uriVariables) throws org.springframework.web.client.RestClientException {
        RestTemplate restTemplate = restTemplateBuilder.build();
        RequestCallback requestCallback = restTemplate.httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = restTemplate.responseEntityExtractor(responseType);
        return restTemplate.execute(url, httpMethod, requestCallback, responseExtractor, uriVariables);
    }
}
