package snw.engine.frame;

import snw.engine.animation.AnimationData;
import snw.engine.component.TopLevelComponent;
import snw.engine.core.Engine;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

public class EngineFrame extends JFrame {
    final TopLevelComponent panel = Engine.getPanel();
    final ContentPanel contentPanel = new ContentPanel();
    private final List<String> handlerNameList = new ArrayList<>();
    private final List<TransferHandler> handlerList = new ArrayList<>();

    public EngineFrame() {
        add(contentPanel);
        resize();
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                Engine.runNewThread(() -> {
                    Engine.exit(0);
                });
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                Engine.runNewThread(() -> {
                    panel.keyTyped(e.getKeyChar());
                });
            }

            @Override
            public void keyPressed(KeyEvent e) {
                Engine.runNewThread(() -> {
                    panel.keyPressed(e.getKeyCode());
                });
            }

            @Override
            public void keyReleased(KeyEvent e) {
                Engine.runNewThread(() -> {
                    panel.keyReleased(e.getKeyCode());
                });
            }
        });

        setTransferHandler(new MainTransferHandler());
    }

    private class ContentPanel extends JPanel {
        public ContentPanel() {
            this.setPreferredSize(new Dimension(panel.getWidth(), panel.getHeight()));
            this.addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    Engine.runNewThread(() -> {
                        panel.mouseClicked(e.getX(), e.getY());
                    });
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    Engine.runNewThread(() -> {
                        panel.mousePressed(e.getX(), e.getY());
                    });
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    Engine.runNewThread(() -> {
                        panel.mouseReleased(e.getX(), e.getY());
                    });
                }

                @Override
                public void mouseEntered(MouseEvent e) {

                    Engine.runNewThread(() -> {
                        panel.mouseEntered();
                    });
                }

                @Override
                public void mouseExited(MouseEvent e) {

                    Engine.runNewThread(() -> {
                        panel.mouseExited();
                    });
                }
            });
            this.addMouseMotionListener(new MouseMotionListener() {

                @Override
                public void mouseMoved(MouseEvent e) {

                    Engine.runNewThread(() -> {
                        panel.mouseMoved(e.getX(), e.getY());
                    });
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    Engine.runNewThread(() -> {
                        panel.mouseDragged(e.getX(), e.getY());
                    });
                }
            });
        }

        @Override
        public void paint(Graphics g) {
            if (panel != null) {
                panel.render((Graphics2D) g, new AnimationData(AffineTransform.getTranslateInstance(0, 0)));
            }
        }

        public void resize() {
            this.setPreferredSize(new Dimension(panel.getWidth(), panel.getHeight()));
        }
    }

    private class MainTransferHandler extends TransferHandler {
        @Override
        public boolean canImport(TransferSupport support) {
            for (TransferHandler handler : handlerList) {
                if (handler.canImport(support)) return true;
            }

            return false;
        }

        @Override
        public boolean importData(TransferSupport support) {
            for (TransferHandler handler : handlerList) {
                if (handler.canImport(support) && handler.importData(support)) return true;
            }

            return false;
        }
    }

    public void setCustomCursor(boolean isCustom) {
        if (isCustom) {
            setCursor(getToolkit().createCustomCursor(
                    new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), null));
            panel.setCursor(true);
        } else {
            setCursor(Cursor.getDefaultCursor());
            panel.setCursor(false);
        }
    }

/*    @Override
    public void requestFocus() {
        super.requestFocus();
        contentPanel.requestFocus();
    }*/

    /**
     * If a handler with the same name has been in the list, it will be removed,
     * and the new one will be added to the end of the list
     *
     * @param name
     * @param handler
     * @return <tt>true<tt/> if overwritten
     */
    public boolean addTransferHandler(String name, TransferHandler handler) {
        boolean contains = false;

        if (handlerNameList.contains(name)) {
            removeTransferHandler(name);
            contains = true;
        }

        handlerNameList.add(name);
        handlerList.add(handler);

        return contains;
    }

    public boolean removeTransferHandler(String name) {
        if (!handlerNameList.contains(name)) return false;

        int index = handlerNameList.indexOf(name);

        handlerNameList.remove(index);
        handlerList.remove(index);

        return true;
    }

    public List<String> getTransferHandlerNameList() {
        return new ArrayList<>(handlerNameList);
    }

    public List<TransferHandler> getTransferHandlerList() {
        return new ArrayList<>(handlerList);
    }


    public void resize() {
        contentPanel.resize();
        pack();
        setLocationRelativeTo(null);
    }
}