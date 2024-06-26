/*
 * Copyright contributors to Hyperledger Besu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.hyperledger.besu.ethereum.mainnet.blockhash;

import static org.hyperledger.besu.evm.operation.BlockHashOperation.BlockHashLookup;

import org.hyperledger.besu.ethereum.chain.Blockchain;
import org.hyperledger.besu.ethereum.core.MutableWorldState;
import org.hyperledger.besu.ethereum.mainnet.ParentBeaconBlockRootHelper;
import org.hyperledger.besu.ethereum.vm.CachingBlockHashLookup;
import org.hyperledger.besu.evm.worldstate.WorldUpdater;
import org.hyperledger.besu.plugin.data.ProcessableBlockHeader;

/** Processes the beacon block storage if it is present in the block header. */
public class CancunBlockHashProcessor implements BlockHashProcessor {

  @Override
  public BlockHashLookup getBlockHashLookup(
      final ProcessableBlockHeader currentHeader, final Blockchain blockchain) {
    return new CachingBlockHashLookup(currentHeader, blockchain);
  }

  @Override
  public void processBlockHashes(
      final Blockchain blockchain,
      final MutableWorldState mutableWorldState,
      final ProcessableBlockHeader currentBlockHeader) {
    currentBlockHeader
        .getParentBeaconBlockRoot()
        .ifPresent(
            beaconBlockRoot -> {
              if (!beaconBlockRoot.isEmpty()) {
                WorldUpdater worldUpdater = mutableWorldState.updater();
                ParentBeaconBlockRootHelper.storeParentBeaconBlockRoot(
                    worldUpdater, currentBlockHeader.getTimestamp(), beaconBlockRoot);
                worldUpdater.commit();
              }
            });
  }
}
