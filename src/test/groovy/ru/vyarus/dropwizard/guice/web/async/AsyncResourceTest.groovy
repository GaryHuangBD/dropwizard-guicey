package ru.vyarus.dropwizard.guice.web.async

import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.glassfish.jersey.server.ManagedAsync
import ru.vyarus.dropwizard.guice.GuiceBundle
import ru.vyarus.dropwizard.guice.admin.AdminRestBundle
import ru.vyarus.dropwizard.guice.test.spock.UseDropwizardApp
import spock.lang.Specification

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended
import java.util.concurrent.CompletableFuture


/**
 * @author Vyacheslav Rusakov
 * @since 18.08.2016
 */
@UseDropwizardApp(AsyncRestApp)
class AsyncResourceTest extends Specification {

    def "Check async resource"() {

        expect: "resource works"
        new URL("http://localhost:8080/async").getText() == 'done!'
        new URL("http://localhost:8080/async/managed").getText() == 'done managed!'

        and: "admin rest works"
        new URL("http://localhost:8081/api/async").getText() == 'done!'
        new URL("http://localhost:8081/api/async/managed").getText() == 'done managed!'
    }

    static class AsyncRestApp extends Application<Configuration> {

        @Override
        void initialize(Bootstrap<Configuration> bootstrap) {
            bootstrap.addBundle(new AdminRestBundle("/api/*"))
            bootstrap.addBundle(GuiceBundle.builder()
                    .extensions(AsyncResource)
                    .build())
        }

        @Override
        void run(Configuration configuration, Environment environment) throws Exception {

        }
    }

    @Path("/async")
    static class AsyncResource {

        @GET
        public void asyncGet(@Suspended final AsyncResponse asyncResponse) {
            String thread = Thread.currentThread().name
            CompletableFuture
                    .runAsync({
                assert thread != Thread.currentThread().name
                println "expensive async task"
                sleep(200)
            })
                    .thenApply({ result -> asyncResponse.resume("done!") });
        }

        @GET
        @ManagedAsync
        @Path("/managed")
        public void managedAsyncGet(@Suspended final AsyncResponse asyncResponse) {
            println "expensive managed async task";
            sleep(200)
            asyncResponse.resume("done managed!")
        }
    }
}