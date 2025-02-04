# Praktikum Softwaretechnik 2 (ST2) im SoSe 2024

Sofortiges Feedback zu Ihrer Lösung finden Sie wie immer auf Ihrer 
[individuellen Testseite](http://students.pages.archi-lab.io/st2/ss24/m4/test/ST2M4_tests_group_f2e37a57-547a-4480-9f47-73a034b199fa).

_Da wir aus der Aufgabenbeschreibung direkt Coding-Aufgaben ableiten, ist die komplette
Beschreibung des Meilenstein-Pflichtteils in Englisch gehalten._


## # Milestone 4 (M4) - REST API Implementation and Use 

For a selected set of REST end points in our ecommerce system, you will to implement the API. In addition, you
will use the API by using REST tools like `curl` or `Postman`.

The "use case interfaces" against which you have coded in M0 ... M3 would strictly speaking now be obsolete, since
we could handle all the use cases with a full REST API. However, this would make your implementation task quite
big, since we would need a complete REST API for all aggregates. Therefore, we will keep the use case interfaces
in M4. In addition, we keep those "old" interface tests from M3 that are not covered by the new REST-based tests. 
You can treat them as "regression tests" for your code. (Regression tests make sure that everything still works 
when you apply changes.)

Retaining the use case interfaces also allows to provide you with another (actually quite cool) feature.
When you start your main application (right mouse click on the `ProjectApplication.main(...)` and 
choose "Run ..."), then your API starts up and is available under `http://localhost:8080`, with all the test
data pre-loaded. You can then use a REST tool like `curl` or `Postman` to try out your own API.


### Glossary

To make it easier, here is your English-German glossary of the most relevant terms in this milestone. Please use 
the English terms in your code.

| English                           | German                                                                                                                |
|-----------------------------------|-----------------------------------------------------------------------------------------------------------------------|
| `StorageUnit`                | Warenhaus des Shops                                                                                           |
| `HomeAddress`            | Die zu einem Warenhaus gehörende postalische Adresse                                                          |
| `Thing`                   | zu verkaufende Ware                                                                                                        |
| `StockLevel`             | Warenbestand für eine zu verkaufende Ware in einem Warenhaus                                                    |
| `Client`                   | Kunde                                                                                                        |
| `ShoppingBasket`                   | Warenkorb eines Kunde                                                                                   |
| `ShoppingBasketPart`               | Warenkorb-Position für ein zu verkaufende Ware (mit Mengenangabe) in einem Warenkorb                                  |
| `Order`                    | Bestellung                                                                                                         |
| `DeliveryPackage`     | Eine Sendung mit einer oder mehreren Waren, die der Kunde gekauft hat, versendet aus einem Warenhaus |
| `DeliveryPackagePart` | Ein Teil einer Sendung, der ein zu verkaufende Ware und die Menge enthält                                                  |


  
### E1: Implement parts of the REST API from M3

![Bloom-Level](./images/4-filled-32.png)
[Level 4 (Analyse) in Bloom's Taxonomy](https://www.archi-lab.io/infopages/didactics/blooms_taxonomy.html#level4)

In this exercise, you will implement the following 6 of the 20 REST endpoints introduced in M3. If there
are specific requirements with respect to request or response body, they are listed below.


#### (2) Query a specific client by `email`

The REST call needs to return a JSON object in the response body with the following properties:
- The client's `id`
- `name` of the client
- email of that client as an `Email` object. This will be a nested
  property called `email`, which in turn contains the actual email address as 
  a string property called `emailString`.
- `HomeAddress` as nested property. We assume that this nested property contains
   `street`, `city`, and zip code. 
   - zip code is then again a nested property, with `zipCodeString` as a string property
     containing the actual zip code.

The call to `/clients` (without the `email=...` parameter) is forbidden for 
privacy reasons. Your implementation must ensure this. 


#### (3) Return the shopping basket for that specific client, identified by `clientId`

The REST call needs to return a JSON object in the response body with the following properties:
- The shopping basket `id`
- The total value of the shopping basket in a string property `totalsalesPrice`, formatted like "21,34 €" or "0,00 CHF"
- At property `shoppingBasketParts`, an array of nested shopping basket parts, each with the following properties:
  - `thingId` of the thing in this shopping basket part
  - `quantity` of the thing in this shopping basket part

The call to `/shoppingBaskets` (without the `clientId=...` parameter) is forbidden for
privacy reasons. Your implementation must ensure this.


#### (6) Add a specific thing (given by its thingId) to the shopping basket (with a given quantity)

The request body is expected to contain a JSON object with the following properties:
- The `thingId` of the thing to be added
- The `quantity` of the thing to be added
The response body can be empty.

#### (10) Delete a certain thing from the shopping basket

#### (11) Check out the shopping basket (and have the shop create an order as a consequence)

The request body can be empty. The response body must contain the `orderId` of the newly 
created order.


#### (14) Query all delivery packages for a given order

The response body must contain an array of JSON objects for the delivery packages, each with the following properties:
- `id` of the delivery package
- `storageUnitId` of the storage unit from which the delivery package was sent
- `orderId` for the corresponding order
- an array called `deliveryPackageParts` containing the quantities of the things in this delivery package: 
  - `thingId` of the thing
  - `quantity` of the thing in this delivery package

The call to `/deliveryPackages` (without the `orderId=...` parameter) is forbidden for
security reasons. Your implementation must ensure this.



### Additional hints for the REST APIs

#### General hints 

- Use the `@RestController` annotations for your REST controllers.
- Rest controllers belong to the application layer. 
- Keep the rest controller classes small. Conversions and business logic should be done in your application services.


#### Specific hints for the endpoints

You have specifified the URIs for these endpoints in M3. Here, again, are the additional hints for the implementation:

| Refers to | Hint                                                                                                                                                                                                                                                                         |
|-----------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| (all)     | Always ask yourself first to which aggregate this endpoint belongs. This determines the first part of the URI.                                                                                                                                                               |
| 6         | Assume that the shopping basket endpoint has a sub-resource `parts`, which manages the shopping basket parts.                                                                                                                                                                    |
| 10        | Assume that the ID of a shopping basket part resources is the `thingId` of the corresponding thing.                                                                                                                                                                       |
| 11        | Assume that a shopping basket has a sub-resource `checkout`, which can be empty or not. (Which verb should you use then?)                                                                                                                                                           |


#### Naming conventions for REST endpoints

You must stick to the following conventions (otherwise the table checking test will fail):

* Lower CamelCase for REST endpoints
  - We use and expect the "lower CamelCase" way of writing a REST endpoints, both for URIs and for IDs.
    I.e. a class called `ArchiLabDocument` will have a REST endpoint like `GET /archiLabDocuments/{archiLabDocument-id}`.
* When you specify the REST URIs above, you **must** write IDs in the following way:
  - `/largeAnimals/{largeAnimal-id}`
  - `/evenLargerAnimals/{evenLargerAnimal-id}`
* When you specify query-type REST endpoints, you need to write the query syntax without spaces, and express the query
  value by `...`
  - Assume that you have a REST endpoint that finds all large animals where `color` matches a given search color,
    then you need to write the URI as `/largeAnimals?color=...`.
* To access the last element of a list sorted according to time, end your query with the parameter `filter=latest`


#### HTTP Response Codes

Please use the following HTTP response codes for your REST API:
- `201 Created` for successfully creating something new
- `200 OK` for any other successful operation, including successful DELETE operation (for simplicity reasons)
- `404 Not Found` if a resource does not exist
- `405 Method Not Allowed` if this operation is generally not allowed on this resource
- `409 Conflict` executing this operation would cause a conflict on server side, e.g. by violating a business rule
- `422 Unprocessable Entity` if the request is syntactically correct, but semantically wrong, e.g. by
  submitting invalid data.


### E2: Learn to use a REST API
After creating your REST API it is now time to learn how to use a provided API.
At [https://apilab.archi-lab.io](https://apilab.archi-lab.io) you will find a mocked REST API that provides the following functions:
* Querying customers by their ID
* Querying products by their name
* Creating new Products

The table in [**`main/resources/E2.md`**](src/main/resources/E2.md) contains some questions. Call the provided API to answer them.

#### Hints
* There are many different ways to communicate with the API. Some examples are the `curl`command, Postman or even your browser.
* if you call the above URL you will get a 404 error. This is normal, since you haven't specified any resources yet.
* use the endpoints `/customers` and `/products`
* use the JSON format for your request body (if needed)
* the API is case sensitive


## Zusatzaufgabe (Optional): Nutzung von KI-Tools für Algorithmus-Entwicklung und API-Design

_(Ab jetzt wieder auf Deutsch, weil Sie die Videos für die Zusatzaufgabe auf Deutsch erstellen können.)_

Wie in [der ST2-Seite beschrieben](https://www.archi-lab.io/regularModules/ss24/st2_ss24.html#ki-tools-im-praktikum), 
können Sie KI-Tools einsetzen und Ihre Erfahrungen damit reflektieren. Sie können damit bis zu 6 
Klausurbonuspunkte sammeln.

In diesem Milestone schauen wir besonders auf das Thema **KI-Tools mit Algorithmen und API-Design**. Sie können das
anwenden, wenn Sie sich mit der Implementierung von des `DeliveryPackage`-Algorithmus beschäftigen, 
aber auch bei der Spezifikation der REST-API. Beachten Sie aber, dass wir ganz bestimmte Regeln für das API-Design
vorgeben (siehe die Vorlesungsvideos) - die müssen Sie der KI dann "beibringen". 

Ihr maximal 5-minütiges Video soll bitte folgende Leitfragen beantworten:

1. Bei welchem Teil des Entwicklung sollten die KI-Tools Ihnen helfen?
2. Welche(s) KI-Tool(s) haben Sie verwendet? Wenn in Kombination, welches Tool an welcher Stelle?
3. Was hat gut funktioniert? 
4. Was hat nicht so gut funktioniert?
5. Was ist Ihr Fazit, wenn Sie Ihren Kommilitonen eine Empfehlung geben wollen?


### Anmeldung und Abgabe

Sie müssen sich für die Zusatzaufgabe anmelden, durch Beitritt 
[zu dieser ILU-Gruppe](https://ilu.th-koeln.de/ilias.php?baseClass=ilrepositorygui&ref_id=404289).
Der Beitritt wird im M3-Workshop freigeschaltet. Es gibt nur eine begrenzte Anzahl Plätze. Es gilt: 
- First come, first serve
- Jede:r hat nur eine Anmeldung zu einem Video frei. Wenn man also einen Slot bekommt und dann verfallen lässt, 
  dann gibt es keinen weiteren Versuch.

Die Abgabe erfolgt auch über ILU. Sie laden Ihr Video in die Übung "KI-Reflektions-Video" hoch. (Achtung: 
maximale Dateigröße 350 MB - das sollte für 5 min 1080p leicht ausreichen.) Abgabedatum ist dasselbe wie der 
Pflichtteil des Meilensteins. Feedback gibt es dann von uns über eine persönliche Nachricht. Je nach 
Qualität der Reflektion im Video geben wir Ihnen zwischen 0 und 6 Klausur-Bonuspunkte.

<b>Mit der Abgabe des Videos stimmen Sie zu, dass wir Ihr Video für nicht-kommerzielle Zwecke im
Rahmen von Forschung und Lehre auswerten dürfen, in Teilen oder ganz in Lehrveranstaltungen zeigen können, 
sowie in weiteren Lehrmitteln (z.B. Lehrvideos) verwenden dürfen. Wenn von Ihnen gewünscht, wird Ihre
Urheberschaft dabei anonymisiert.</b>


### Am Schluss nochmal Disclaimers ...

- KI-Tool-Nutzung im Praktikum ist **NICHT** Cheating! Egal, ob Sie dazu ein Bonus-Video machen oder 
  nicht. (Copy-Paste von Kommilitonen ist aber immer Cheating.) 
- Sie dürfen die KI-Tools auch nutzen, ohne dass Sie ein Bonus-Video machen. Dann gibt es halt keine Bonuspunkte.
- In der Klausur gibts aber keine KI-Tools! Da müssen Sie dann schon selbst coden :-). Sie müssen es also selbst können.
