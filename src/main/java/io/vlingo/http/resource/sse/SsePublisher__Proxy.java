// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.http.resource.sse;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;

public class SsePublisher__Proxy implements io.vlingo.http.resource.sse.SsePublisher {

  private static final String subscribeRepresentation1 = "subscribe(io.vlingo.http.resource.sse.SseSubscriber)";
  private static final String unsubscribeRepresentation2 = "unsubscribe(io.vlingo.http.resource.sse.SseSubscriber)";
  private static final String stopRepresentation3 = "stop()";

  private final Actor actor;
  private final Mailbox mailbox;

  public SsePublisher__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  public void subscribe(io.vlingo.http.resource.sse.SseSubscriber arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<SsePublisher> consumer = (actor) -> actor.subscribe(arg0);
      if (mailbox.isPreallocated()) { mailbox.send(actor, SsePublisher.class, consumer, null, subscribeRepresentation1); }
      else { mailbox.send(new LocalMessage<SsePublisher>(actor, SsePublisher.class, consumer, subscribeRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, subscribeRepresentation1));
    }
  }
  @Override
  public void unsubscribe(io.vlingo.http.resource.sse.SseSubscriber arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<SsePublisher> consumer = (actor) -> actor.unsubscribe(arg0);
      if (mailbox.isPreallocated()) { mailbox.send(actor, SsePublisher.class, consumer, null, unsubscribeRepresentation2); }
      else { mailbox.send(new LocalMessage<SsePublisher>(actor, SsePublisher.class, consumer, unsubscribeRepresentation2)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, unsubscribeRepresentation2));
    }
  }
  @Override
  public void stop() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<SsePublisher> consumer = (actor) -> actor.stop();
      if (mailbox.isPreallocated()) { mailbox.send(actor, SsePublisher.class, consumer, null, stopRepresentation3); }
      else { mailbox.send(new LocalMessage<SsePublisher>(actor, SsePublisher.class, consumer, stopRepresentation3)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, stopRepresentation3));
    }
  }
  @Override
  public boolean isStopped() {
    return actor.isStopped();
  }
}