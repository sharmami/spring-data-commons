/*
 * Copyright 2008-2012 the original author or authors.
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
package org.springframework.data.repository.query;

import static org.springframework.data.repository.util.ClassUtils.*;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.core.EntityMetadata;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.util.Assert;

/**
 * Abstraction of a method that is designated to execute a finder query. Enriches the standard {@link Method} interface
 * with specific information that is necessary to construct {@link RepositoryQuery}s for the method.
 * 
 * @author Oliver Gierke
 */
public class QueryMethod {

	private final RepositoryMetadata metadata;
	private final Method method;
	private final Parameters parameters;

	/**
	 * Creates a new {@link QueryMethod} from the given parameters. Looks up the correct query to use for following
	 * invocations of the method given.
	 * 
	 * @param method must not be {@literal null}
	 * @param metadata must not be {@literal null}
	 */
	public QueryMethod(Method method, RepositoryMetadata metadata) {

		Assert.notNull(method, "Method must not be null!");
		Assert.notNull(metadata, "Repository metadata must not be null!");

		for (Class<?> type : Parameters.TYPES) {
			if (getNumberOfOccurences(method, type) > 1) {
				throw new IllegalStateException(
						String.format("Method must only one argument of type %s!", type.getSimpleName()));
			}
		}

		if (hasParameterOfType(method, Pageable.class)) {
			assertReturnTypeAssignable(method, Page.class, List.class);
			if (hasParameterOfType(method, Sort.class)) {
				throw new IllegalStateException("Method must not have Pageable *and* Sort parameter. "
						+ "Use sorting capabilities on Pageble instead!");
			}
		}

		this.method = method;
		this.parameters = createParameters(method);
		this.metadata = metadata;

		Assert.notNull(this.parameters);

		if (isPageQuery()) {
			Assert.isTrue(this.parameters.hasPageableParameter(), "Paging query needs to have a Pageable parameter!");
		}
	}

	/**
	 * Creates a {@link Parameters} instance.
	 * 
	 * @param method
	 * @return must not return {@literal null}.
	 */
	protected Parameters createParameters(Method method) {
		return new Parameters(method);
	}

	/**
	 * Returns the method's name.
	 * 
	 * @return
	 */
	public String getName() {

		return method.getName();
	}

	@SuppressWarnings("rawtypes")
	public EntityMetadata<?> getEntityInformation() {

		return new EntityMetadata() {

			public Class<?> getJavaType() {

				return getDomainClass();
			}
		};
	}

	/**
	 * Returns the name of the named query this method belongs to.
	 * 
	 * @return
	 */
	public String getNamedQueryName() {

		Class<?> domainClass = getDomainClass();
		return String.format("%s.%s", domainClass.getSimpleName(), method.getName());
	}

	/**
	 * Returns the domain class the query method is targeted at.
	 * 
	 * @return
	 */
	protected Class<?> getDomainClass() {

		Class<?> repositoryDomainClass = metadata.getDomainType();
		Class<?> methodDomainClass = metadata.getReturnedDomainClass(method);

		return repositoryDomainClass == null || repositoryDomainClass.isAssignableFrom(methodDomainClass) ? methodDomainClass
				: repositoryDomainClass;
	}

	/**
	 * Returns the type of the object that will be returned.
	 * 
	 * @return
	 */
	public Class<?> getReturnedObjectType() {
		return metadata.getReturnedDomainClass(method);
	}

	/**
	 * Returns whether the finder will actually return a collection of entities or a single one.
	 * 
	 * @return
	 */
	public boolean isCollectionQuery() {

		Class<?> returnType = method.getReturnType();
		return !isPageQuery() && org.springframework.util.ClassUtils.isAssignable(Iterable.class, returnType);
	}

	/**
	 * Returns whether the finder will return a {@link Page} of results.
	 * 
	 * @return
	 */
	public boolean isPageQuery() {

		Class<?> returnType = method.getReturnType();
		return org.springframework.util.ClassUtils.isAssignable(Page.class, returnType);
	}

	public boolean isModifyingQuery() {

		return false;
	}

	/**
	 * Returns the {@link Parameters} wrapper to gain additional information about {@link Method} parameters.
	 * 
	 * @return
	 */
	public Parameters getParameters() {

		return parameters;
	}

	/*
			 * (non-Javadoc)
			 *
			 * @see java.lang.Object#toString()
			 */
	@Override
	public String toString() {

		return method.toString();
	}
}
