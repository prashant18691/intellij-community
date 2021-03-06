// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.ui.panel;

import com.intellij.openapi.ui.panel.PanelBuilder;
import com.intellij.openapi.ui.panel.PanelGridBuilder;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelGridBuilderImpl<T extends PanelBuilder> implements PanelGridBuilder<T> {
  private boolean expand;
  private final List<T> builders = new ArrayList<>();

  @Override
  public PanelGridBuilder<T> add(@NotNull T builder) {
    builders.add(builder);
    return this;
  }

  @Override
  public PanelGridBuilder<T> expandVertically(boolean expand) {
    this.expand = expand;
    return this;
  }

  @Override
  @NotNull
  public JPanel createPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
                                                   null, 0, 0);

    addToPanel(panel, gc);
    return panel;
  }

  @Override
  public boolean constrainsValid() {
    return builders.stream().allMatch(b -> b.constrainsValid());
  }

  public int gridWidth() {
    return builders.stream().map(b -> b.gridWidth()).max(Integer::compareTo).orElse(0);
  }

  @Override
  public void addToPanel(JPanel panel, GridBagConstraints gc) {
    builders.stream().filter(b -> b.constrainsValid()).forEach(b -> b.addToPanel(panel, gc));

    if (!expand) {
      gc.gridx = 0;
      gc.anchor = GridBagConstraints.PAGE_END;
      gc.fill = GridBagConstraints.BOTH;
      gc.weighty = 1.0;
      gc.insets = JBUI.insets(0);
      gc.gridwidth = gridWidth();
      panel.add(new JPanel(), gc);
    }
  }
}
