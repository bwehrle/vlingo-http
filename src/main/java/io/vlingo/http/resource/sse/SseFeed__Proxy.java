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

public class SseFeed__Proxy implements io.vlingo.http.resource.sse.SseFeed {

  private static final String toRepresentation1 = "to(java.util.Collection<io.vlingo.http.resource.sse.SseSubscriber>)";

  private final Actor actor;
  private final Mailbox mailbox;

  public SseFeed__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  public void to(java.util.Collection<io.vlingo.http.resource.sse.SseSubscriber> arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<SseFeed> consumer = (actor) -> actor.to(arg0);
      if (mailbox.isPreallocated()) { mailbox.send(actor, SseFeed.class, consumer, null, toRepresentation1); }
      else { mailbox.send(new LocalMessage<SseFeed>(actor, SseFeed.class, consumer, toRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, toRepresentation1));
    }
  }
}
