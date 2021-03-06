/*
 * Copyright 2008-2010 the original author or authors.
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
package org.springframework.data.repository.config;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.parsing.ReaderContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Unit test for {@link TypeFilterParser}.
 * 
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.class)
public class TypeFilterParserUnitTests {

	private TypeFilterParser parser;
	private Element documentElement;

	@Mock
	private ClassLoader classLoader;

	@Mock
	private ReaderContext context;

	@Mock
	private ClassPathScanningCandidateComponentProvider scanner;

	@Before
	public void setUp() throws SAXException, IOException, ParserConfigurationException {

		parser = new TypeFilterParser(classLoader, context);

		Resource sampleXmlFile = new ClassPathResource("type-filter-test.xml", TypeFilterParserUnitTests.class);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);

		documentElement = factory.newDocumentBuilder().parse(sampleXmlFile.getInputStream()).getDocumentElement();
	}

	@Test
	public void parsesIncludesCorrectly() throws Exception {

		Element element = DomUtils.getChildElementByTagName(documentElement, "firstSample");

		parser.parseFilters(element, scanner);

		verify(scanner, atLeastOnce()).addIncludeFilter(isA(AssignableTypeFilter.class));
	}

	@Test
	public void parsesExcludesCorrectly() throws Exception {

		Element element = DomUtils.getChildElementByTagName(documentElement, "secondSample");

		parser.parseFilters(element, scanner);

		verify(scanner, atLeastOnce()).addExcludeFilter(isA(AssignableTypeFilter.class));
	}
}
