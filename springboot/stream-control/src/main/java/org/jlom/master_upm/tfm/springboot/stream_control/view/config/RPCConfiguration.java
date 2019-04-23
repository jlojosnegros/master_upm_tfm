package org.jlom.master_upm.tfm.springboot.stream_control.view.config;


import org.jlom.master_upm.tfm.springboot.stream_control.view.StreamControlView;
import org.jlom.master_upm.tfm.springboot.stream_control.view.api.StreamControlInterface;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.remoting.service.AmqpInvokerServiceExporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Configuration
public class RPCConfiguration {

  @Bean
  StreamControlInterface streamControlInterface() {
    return new StreamControlView();
  }

  Queue queue() {
    return new Queue("stream-control-queue");
  }

  @Bean
  AmqpInvokerServiceExporter exporter(
          StreamControlInterface implementation, AmqpTemplate template) {

    AmqpInvokerServiceExporter exporter = new AmqpInvokerServiceExporter();
    exporter.setServiceInterface(StreamControlInterface.class);
    exporter.setService(implementation);
    exporter.setAmqpTemplate(template);
    return exporter;
  }

  @Bean
  SimpleMessageListenerContainer listener(
          ConnectionFactory factory,
          AmqpInvokerServiceExporter exporter,
          Queue queue) {

    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(factory);
    container.setMessageListener(exporter);
    container.setQueueNames(queue.getName());
    return container;
  }
}
