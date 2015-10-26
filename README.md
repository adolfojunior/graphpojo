# graphpojo
GraphQL Pojo Mapping.

Java Sample
```Java
GraphPojoSchema schema = new GraphPojoSchemaBuilder()
	.add(Category.class, new CategoryFetcher());
	.add(Product.class, new ProductFetcher());
	.build();

String query = "query GetProduct { Product { id name categories {name} } }";

Map<String, Object> queryResult = schema.execute(query);

System.out.println(queryResult);
```

Will print:
```
{SampleProduct={id=1, name=Product 1}}
```