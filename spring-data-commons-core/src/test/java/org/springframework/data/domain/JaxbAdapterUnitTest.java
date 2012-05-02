/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.domain;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.junit.Test;

/**
 * 
 * @author Oliver Gierke
 */
public class JaxbAdapterUnitTest {

	@Test
	public void usesCustomTypeAdapterForPageRequests() throws JAXBException {

		JAXBContext context = JAXBContext.newInstance(JaxbAdapters.class.getPackage().getName());
		Marshaller marshaller = context.createMarshaller();

		Foo object = new Foo();
		object.request = new PageRequest(2, 15);

		StringWriter writer = new StringWriter();
		marshaller.marshal(object, writer);

		System.out.println(writer);
	}

	@XmlRootElement
	class Foo {

		@XmlJavaTypeAdapter(JaxbAdapters.PageRequestAdapter.class)
		PageRequest request;
	}
}
