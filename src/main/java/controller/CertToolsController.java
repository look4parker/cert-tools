package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping(value = "/certtools")
public class CertToolsController {

        private final static Logger LOGGER = LoggerFactory.getLogger( CertToolsController.class );

        @RequestMapping( value="/create-certs", method = RequestMethod.POST, consumes = "application/json")
        public DeferredResult<String> batchRequest(@RequestParam( value="val", required = false ) String timeWindow) {
            LOGGER.info("Received request to create certificates.");
            return new DeferredResult<String>();
        }

}
