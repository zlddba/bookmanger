package org.zl.team.util

import javax.swing.JOptionPane

object AlertUtil {

    fun showInfo(title: String, message: String) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE)
    }

    fun showWarning(title: String, message: String) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE)
    }

    fun showError(title: String, message: String) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE)
    }

    fun showConfirm(title: String, message: String): Boolean {
        val result = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION)
        return result == JOptionPane.YES_OPTION
    }
}
