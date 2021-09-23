package com.demidovn.fruitbounty.server.config;

import com.demidovn.fruitbounty.server.AppConfigs;
import com.demidovn.fruitbounty.server.config.websocket.handlers.GameServerSubProtocolWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurationSupport;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import java.util.List;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends WebSocketMessageBrokerConfigurationSupport implements WebSocketMessageBrokerConfigurer {

  private static final int MESSAGE_SIZE_LIMIT = 512;

  @Bean
  public WebSocketHandler subProtocolWebSocketHandler() {
    return new GameServerSubProtocolWebSocketHandler(clientInboundChannel(), clientOutboundChannel());
  }

  @Bean
  public ServletServerContainerFactoryBean createWebSocketContainer() {
    ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
    container.setMaxTextMessageBufferSize(MESSAGE_SIZE_LIMIT);
    container.setMaxBinaryMessageBufferSize(MESSAGE_SIZE_LIMIT);
    return container;
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/queue", "/topic");
    config.setApplicationDestinationPrefixes("/app");
    config.setUserDestinationPrefix("/user");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint(AppConfigs.CONNECT_WEBSOCKET_URL).setAllowedOrigins("*").withSockJS();

//    registry.addEndpoint(AppConfigs.CONNECT_WEBSOCKET_URL)
//            .setAllowedOrigins("vkfile://mini.app.host.com")
//            .setAllowedOrigins("vkfile://qrappm.juice.vk-apps.com")
//            .setAllowedOrigins("mini.app.host.com")
//            .setAllowedOrigins("vk-apps.com")
//            .setAllowedOrigins("qrappm.juice.vk-apps.com")
//            .withSockJS();
  }

  @Override
  public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
    super.configureWebSocketTransport(registry);
    registry.setMessageSizeLimit(MESSAGE_SIZE_LIMIT);
  }

  @Override
  public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
    return super.configureMessageConverters(messageConverters);
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    super.configureClientInboundChannel(registration);
  }

  @Override
  public void configureClientOutboundChannel(ChannelRegistration registration) {
    super.configureClientOutboundChannel(registration);
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    super.addArgumentResolvers(argumentResolvers);
  }

  @Override
  public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
    super.addReturnValueHandlers(returnValueHandlers);
  }

}
