/*
 * Copyright 2004-2005 the original author or authors.
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
package org.codehaus.groovy.grails.plugins.web.mimes

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.codehaus.groovy.grails.web.mime.MimeType
import org.codehaus.groovy.grails.web.mime.MimeTypeProvider
import org.springframework.beans.factory.FactoryBean
import org.codehaus.groovy.grails.plugins.support.aware.GrailsApplicationAware
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext

/**
 * Creates the MimeType[] object that defines the configured mime types.
 *
 * @author Graeme Rocher
 * @since 2.0
 */
@CompileStatic
class MimeTypesFactoryBean implements FactoryBean<MimeType[]>, ApplicationContextAware{

    ApplicationContext applicationContext
    GrailsApplication grailsApplication

    private MimeType[] mimeTypes

    MimeType[] getObject() {
        Collection<MimeTypeProvider> mimeTypeProviders = applicationContext ? applicationContext.getBeansOfType(MimeTypeProvider).values() : new ArrayList<MimeTypeProvider>()
        final grailsApplication = this.grailsApplication ?: applicationContext.getBean(GrailsApplication)
        def config = grailsApplication?.config
        def mimeConfig = getMimeConfig(config)
        if (!mimeConfig) {
            mimeTypes = MimeType.createDefaults()
            return mimeTypes
        }

        def mimes = []
        for (entry in mimeConfig.entrySet()) {
            if (entry.value instanceof List) {
                for (i in entry.value) {
                    mimes << new MimeType(i.toString(),entry.key.toString())
                }
            }
            else {
                mimes << new MimeType(entry.value.toString(), entry.key.toString())
            }
        }
        for(MimeTypeProvider mtp in mimeTypeProviders) {
            for(MimeType mt in mtp.mimeTypes) {
                if (!mimes.contains(mt)) {
                    mimes << mt
                }
            }
        }
        mimeTypes = mimes
        mimeTypes

    }

    Class<?> getObjectType() { MimeType[] }

    boolean isSingleton() { true }


    @CompileStatic(TypeCheckingMode.SKIP)
    protected Map<CharSequence, CharSequence> getMimeConfig(ConfigObject config) {
        config?.grails?.mime?.types
    }
}
