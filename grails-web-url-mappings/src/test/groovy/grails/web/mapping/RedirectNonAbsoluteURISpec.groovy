package grails.web.mapping

import grails.util.GrailsWebMockUtil
import grails.web.http.HttpHeaders
import org.springframework.web.context.request.RequestContextHolder
import spock.lang.Issue

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/*
 * Copyright 2014 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Iván López
 */
class RedirectNonAbsoluteURISpec extends AbstractUrlMappingsSpec {

    @Issue('10879')
    void 'An "absolute=true" redirect generates an absolute URL in Location header'() {
        given:
        def linkGenerator = getLinkGenerator {
            "/$controller/$action?/$id?"()
        }
        def responseRedirector = new ResponseRedirector(linkGenerator)
        HttpServletRequest request = Mock(HttpServletRequest) { lookup() >> GrailsWebMockUtil.bindMockWebRequest() }
        HttpServletResponse response = Mock(HttpServletResponse)

        when: 'redirecting with absolute=true'
        responseRedirector.redirect(request, response, [controller: 'test', action: 'foo', absolute: true])

        then: 'the absolute URL is generated'
        1 * response.setStatus(302)
        1 * response.setHeader(HttpHeaders.LOCATION, 'http://localhost/test/foo')

        cleanup:
        RequestContextHolder.setRequestAttributes(null)
    }

    @Issue('10879')
    void 'An "absolute=false" redirect generates an non-absolute URL in Location header'() {
        given:
        def linkGenerator = getLinkGenerator {
            "/$controller/$action?/$id?"()
        }
        def responseRedirector = new ResponseRedirector(linkGenerator)
        HttpServletRequest request = Mock(HttpServletRequest) { lookup() >> GrailsWebMockUtil.bindMockWebRequest() }
        HttpServletResponse response = Mock(HttpServletResponse)

        when: 'redirecting with absolute=false'
        responseRedirector.redirect(request, response, [controller: 'test', action: 'foo', absolute: false])

        then: 'the partial URI is generated'
        1 * response.setStatus(302)
        1 * response.setHeader(HttpHeaders.LOCATION, '/test/foo')

        cleanup:
        RequestContextHolder.setRequestAttributes(null)
    }

    @Issue('10879')
    void 'A redirect without "absolute" parameter generates an absolute URL in Location header by default'() {
        given:
        def linkGenerator = getLinkGenerator {
            "/$controller/$action?/$id?"()
        }
        def responseRedirector = new ResponseRedirector(linkGenerator)
        HttpServletRequest request = Mock(HttpServletRequest) { lookup() >> GrailsWebMockUtil.bindMockWebRequest() }
        HttpServletResponse response = Mock(HttpServletResponse)

        when: 'redirecting without absolute'
        responseRedirector.redirect(request, response, [controller: 'test', action: 'foo'])

        then: 'the absolute URL is generated by default'
        1 * response.setStatus(302)
        1 * response.setHeader(HttpHeaders.LOCATION, 'http://localhost/test/foo')

        cleanup:
        RequestContextHolder.setRequestAttributes(null)
    }

    @Issue('11673')
    void 'An "absolute=false" redirect includes context-path in Location header'() {
        given:
        def linkGenerator = getLinkGeneratorWithContextPath {
            "/$controller/$action?/$id?"()
        }
        def responseRedirector = new ResponseRedirector(linkGenerator)
        HttpServletRequest request = Mock(HttpServletRequest) { lookup() >> GrailsWebMockUtil.bindMockWebRequest() }
        HttpServletResponse response = Mock(HttpServletResponse)

        when: 'redirecting with absolute=false where context-path is set'
        responseRedirector.redirect(request, response, [controller: 'test', action: 'foo', absolute: false])

        then: 'the partial URI includes context-path'
        1 * response.setStatus(302)
        1 * response.setHeader(HttpHeaders.LOCATION, CONTEXT_PATH + '/test/foo')

        cleanup:
        RequestContextHolder.setRequestAttributes(null)
    }
}
