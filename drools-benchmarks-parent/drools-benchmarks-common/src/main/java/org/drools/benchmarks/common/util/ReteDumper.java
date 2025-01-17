/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.benchmarks.common.util;

import org.drools.core.common.BaseNode;
import org.drools.base.common.NetworkNode;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.Sink;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieRuntime;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ReteDumper {

    private ReteDumper() { }

    public static void dumpRete(KieBase kbase ) {
        dumpRete((InternalKnowledgeBase) kbase );
    }

    public static void dumpRete(KieRuntime session ) {
        dumpRete((InternalKnowledgeBase)session.getKieBase() );
    }

    public static void dumpRete(KieSession session ) {
        dumpRete((InternalKnowledgeBase)session.getKieBase() );
    }

    public static void dumpRete(InternalKnowledgeBase kBase ) {
        dumpRete(kBase.getRete());
    }

    public static void dumpRete(Rete rete ) {
        for (EntryPointNode entryPointNode : rete.getEntryPointNodes().values()) {
            dumpNode( entryPointNode, "", new HashSet<BaseNode>() );
        }
    }

    private static void dumpNode( BaseNode node, String ident, Set<BaseNode> visitedNodes ) {
        System.out.println(ident + node + " on " + node.getPartitionId());
        if (!visitedNodes.add( node )) {
            return;
        }
        Sink[] sinks = getSinks( node );
        if (sinks != null) {
            for (Sink sink : sinks) {
                if (sink instanceof BaseNode ) {
                    dumpNode( (BaseNode)sink, ident + "    ", visitedNodes );
                }
            }
        }
    }

    public static Sink[] getSinks( NetworkNode node ) {
        Sink[] sinks = null;
        if (node instanceof EntryPointNode ) {
            EntryPointNode source = (EntryPointNode) node;
            Collection<ObjectTypeNode> otns = source.getObjectTypeNodes().values();
            sinks = otns.toArray(new Sink[otns.size()] );
        } else if (node instanceof ObjectSource ) {
            ObjectSource source = (ObjectSource) node;
            sinks = source.getObjectSinkPropagator().getSinks();
        } else if (node instanceof LeftTupleSource ) {
            LeftTupleSource source = (LeftTupleSource) node;
            sinks = source.getSinkPropagator().getSinks();
        }
        return sinks;
    }
}
