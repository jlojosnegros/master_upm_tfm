package org.jlom.master_upm.tfm.springboot.user_categories.view;

import org.jlom.master_upm.tfm.springboot.user_categories.view.api.StreamControlInterface;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.remoting.client.AmqpProxyFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class RPCTestConfiguration {

  @Bean
  @Primary
  Queue queueTest() {
    return new Queue("stream-control-rpc-queue");
  }

  @Bean
  Exchange directExchange(Queue someQueue) {
    DirectExchange directExchange = new DirectExchange("stream-control.exchange.test");
    BindingBuilder.bind(someQueue)
            .to(directExchange)
            .with("stream-control.routing-key");
    return directExchange;
  }

  @Bean
  RabbitTemplate ampqTemplate(ConnectionFactory factory) {
    RabbitTemplate template = new RabbitTemplate(factory);

    template.setRoutingKey("stream-control.routing-key");
    template.setExchange("stream-control.exchange.test");

    return template;
  }

  AmqpProxyFactoryBean amqpFactoryBean (AmqpTemplate ampqTemlate) {

    AmqpProxyFactoryBean factoryBean = new AmqpProxyFactoryBean();
    factoryBean.setServiceInterface(StreamControlInterface.class);
    factoryBean.setAmqpTemplate(ampqTemlate);

    return factoryBean;
  }
}

