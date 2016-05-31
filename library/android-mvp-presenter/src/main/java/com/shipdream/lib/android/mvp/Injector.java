/*
 * Copyright 2016 Kejun Xia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shipdream.lib.android.mvp;

import com.shipdream.lib.poke.Graph;
import com.shipdream.lib.poke.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Injector {
    static MvpGraph graph;

    /**
     * Get the graph managing injectable objects.
     * @return
     */
    public static MvpGraph getGraph() {
        if (graph == null) {
            final Logger logger = LoggerFactory.getLogger(Injector.class);
            final MvpComponent rootComponent = new MvpComponent("MvpRootComponent");

            graph = new MvpGraph();
            try {
                graph.setRootComponent(rootComponent);
            } catch (Graph.IllegalRootComponentException e) {
                //ignore
            }

            graph.registerDereferencedListener(new Provider.DereferenceListener() {
                @Override
                public void onDereferenced(Provider provider) {
                    if (provider.getReferenceCount() == 0) {
                        Object obj = provider.getCachedInstance();

                        if (obj != null && obj instanceof Bean) {
                            //When the cached instance is still there free and dispose it.
                            Bean bean = (Bean) obj;
                            bean.onDisposed();
                            rootComponent.beans.remove(obj);

                            logger.trace("--Bean freed - '{}'.",
                                    provider.type().getSimpleName());
                        }
                    }
                }

            });
        }
        return graph;
    }



}
