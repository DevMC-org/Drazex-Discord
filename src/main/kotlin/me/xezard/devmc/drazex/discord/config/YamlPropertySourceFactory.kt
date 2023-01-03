package me.xezard.devmc.drazex.vk.config

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.env.PropertySource
import org.springframework.core.io.support.EncodedResource
import org.springframework.core.io.support.PropertySourceFactory
import org.springframework.lang.Nullable

class YamlPropertySourceFactory : PropertySourceFactory {
    override fun createPropertySource(@Nullable name: String?, encodedResource: EncodedResource): PropertySource<*> {
        val factory = YamlPropertiesFactoryBean()
        factory.setResources(encodedResource.resource)
        return PropertiesPropertySource(encodedResource.resource.filename!!, factory.getObject()!!)
    }
}