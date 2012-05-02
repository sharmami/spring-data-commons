/*
 * Copyright 2012 the original author or authors.
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
package org.springframework.data.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

class JaxbAdapters {

	static final String NAMESPACE = "spring-data";

	public static class PageRequestAdapter extends XmlAdapter<PageRequestDto, Pageable> {

		@Override
		public PageRequestDto marshal(Pageable v) throws Exception {
			return PageRequestDto.from(v);
		}

		@Override
		public Pageable unmarshal(PageRequestDto v) throws Exception {
			return v.toPageRequest();
		}
	}

	@XmlType(name = "page-request", namespace = JaxbAdapters.NAMESPACE)
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class PageRequestDto {

		@XmlAttribute
		int page, size;

		@XmlElementWrapper(name = "sort", namespace = JaxbAdapters.NAMESPACE)
		@XmlElement(name = "order", namespace = JaxbAdapters.NAMESPACE)
		List<OrderDto> orders = Collections.emptyList();

		static PageRequestDto from(Pageable request) {

			List<OrderDto> orders = new ArrayList<OrderDto>();
			for (Order order : request.getSort()) {
				orders.add(OrderDto.from(order));
			}

			PageRequestDto dto = new PageRequestDto();
			dto.orders = orders;
			dto.page = request.getPageNumber();
			dto.size = request.getPageSize();

			return dto;
		}

		PageRequest toPageRequest() {

			List<Order> orders = new ArrayList<Order>(this.orders.size());
			for (OrderDto orderSource : this.orders) {
				orders.add(orderSource.toOrder());
			}

			return new PageRequest(page, size, new Sort(orders));
		}
	}

	@XmlType
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class OrderDto {

		@XmlAttribute
		String property;
		@XmlAttribute
		Direction direction;

		private static OrderDto from(Order order) {
			OrderDto dto = new OrderDto();
			dto.direction = order.getDirection();
			dto.property = order.getProperty();
			return dto;
		}

		Order toOrder() {
			return new Order(direction, property);
		}
	}

	public static class PageDto {

	}
}
