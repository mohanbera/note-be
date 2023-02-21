package io.vertx.example;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnection;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() {
        SqlClient sqlPool = startDB(vertx);
        Router router = Router.router(vertx);
        router.get("/login").handler(routingContext -> {

            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/plain");
            response.end("Hello from Vert.x!");
        });

        router.get("/test").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            // response.putHeader("content-type", "application/json");
            // response.end("Hello from Vert.x!");
            getAllData(routingContext, sqlPool);
        });

        vertx.createHttpServer().requestHandler(router).listen(8080);

    }

    public SqlClient startDB(Vertx vertx) {
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setPort(3306)
                .setHost("***")
                .setDatabase("note_db")
                .setUser("***")
                .setPassword("***");

        // Pool options
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(2);

        // Create the client pool
        return MySQLPool.client(vertx, connectOptions, poolOptions);
    }

    private void getAllData(final RoutingContext routingContext, SqlClient client) {
        client
                .query("SELECT * FROM users")
                .execute(ar -> {
                    if (ar.succeeded()) {
                        RowSet<Row> result = ar.result();
                        System.out.println("Got " + result.size() + " rows ");
                    } else {
                        System.out.println("Failure: " + ar.cause().getMessage());
                    }

                    // Now close the pool
                    client.close();
                });
    }
}
