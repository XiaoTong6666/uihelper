/*
 * Copyright (C) 2026 XiaoTong6666
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

package io.github.xiaotong6666.uihelper.miuix.primitive

import androidx.compose.ui.graphics.vector.ImageVector
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Alarm
import top.yukonga.miuix.kmp.icon.extended.Add
import top.yukonga.miuix.kmp.icon.extended.All
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Backup
import top.yukonga.miuix.kmp.icon.extended.ChevronForward
import top.yukonga.miuix.kmp.icon.extended.ContactsCircle
import top.yukonga.miuix.kmp.icon.extended.Copy
import top.yukonga.miuix.kmp.icon.extended.Close
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.icon.extended.Download
import top.yukonga.miuix.kmp.icon.extended.Email
import top.yukonga.miuix.kmp.icon.extended.ExpandMore
import top.yukonga.miuix.kmp.icon.extended.Filter
import top.yukonga.miuix.kmp.icon.extended.Folder
import top.yukonga.miuix.kmp.icon.extended.Forward
import top.yukonga.miuix.kmp.icon.extended.GridView
import top.yukonga.miuix.kmp.icon.extended.Help
import top.yukonga.miuix.kmp.icon.extended.Hide
import top.yukonga.miuix.kmp.icon.extended.Home
import top.yukonga.miuix.kmp.icon.extended.Info
import top.yukonga.miuix.kmp.icon.extended.Link
import top.yukonga.miuix.kmp.icon.extended.ListView
import top.yukonga.miuix.kmp.icon.extended.Lock
import top.yukonga.miuix.kmp.icon.extended.MapAlbum
import top.yukonga.miuix.kmp.icon.extended.MindMap
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.icon.extended.Notes
import top.yukonga.miuix.kmp.icon.extended.Ok
import top.yukonga.miuix.kmp.icon.extended.Phone
import top.yukonga.miuix.kmp.icon.extended.Pin
import top.yukonga.miuix.kmp.icon.extended.Promotions
import top.yukonga.miuix.kmp.icon.extended.Refresh
import top.yukonga.miuix.kmp.icon.extended.Replace
import top.yukonga.miuix.kmp.icon.extended.Report
import top.yukonga.miuix.kmp.icon.extended.Search
import top.yukonga.miuix.kmp.icon.extended.SearchDevice
import top.yukonga.miuix.kmp.icon.extended.Settings
import top.yukonga.miuix.kmp.icon.extended.Show
import top.yukonga.miuix.kmp.icon.extended.Sidebar
import top.yukonga.miuix.kmp.icon.extended.Stopwatch
import top.yukonga.miuix.kmp.icon.extended.Tasks
import top.yukonga.miuix.kmp.icon.extended.Timer
import top.yukonga.miuix.kmp.icon.extended.Translate
import top.yukonga.miuix.kmp.icon.extended.Tune
import top.yukonga.miuix.kmp.icon.extended.Update

/**
 * Converts app-owned Material icon semantics into the nearest native MIUIX glyph.
 *
 * Host apps can keep one semantic icon model while MIUIX surfaces use native glyphs where the
 * mapping is genuinely equivalent. App-specific semantic icons are intentionally preserved rather
 * than collapsed into a generic MIUIX glyph.
 */
internal fun resolveMiuixIcon(source: ImageVector): ImageVector = when (source.name.substringAfterLast('.')) {
    "Add" -> MiuixIcons.Add
    "AccountCircle", "Badge" -> MiuixIcons.ContactsCircle
    "AccountTree", "Hub" -> MiuixIcons.MindMap
    "DisplaySettings" -> source
    "Settings", "SettingsOutline" -> MiuixIcons.Settings
    "AdminPanelSettings", "SettingsEthernet", "SettingsSuggest" -> source
    "Android", "PhoneAndroid" -> MiuixIcons.Phone
    "Apps" -> MiuixIcons.GridView
    "ArrowBack" -> MiuixIcons.Back
    "ArrowForward" -> MiuixIcons.Forward
    "Build" -> MiuixIcons.Tune
    "BugReport" -> source
    "Cable", "NetworkCheck" -> MiuixIcons.Link
    "Calculate" -> MiuixIcons.Tasks
    "Category" -> MiuixIcons.All
    "CheckCircle", "CheckCircleOutline", "Verified" -> MiuixIcons.Ok
    "CloudSync" -> MiuixIcons.Backup
    "Code", "Description", "Source" -> source
    "CompareArrows", "SyncAlt" -> MiuixIcons.Replace
    "ContentCopy" -> MiuixIcons.Copy
    "Close" -> MiuixIcons.Close
    "CrisisAlert", "ErrorOutline", "GppBad", "Warning", "WarningAmber" -> MiuixIcons.Report
    "Details" -> MiuixIcons.ListView
    "Delete" -> MiuixIcons.Delete
    "Dns" -> MiuixIcons.SearchDevice
    "Email" -> MiuixIcons.Email
    "ExpandMore" -> MiuixIcons.ExpandMore
    "FactCheck" -> MiuixIcons.Tasks
    "FileDownload", "VerticalAlignBottom" -> MiuixIcons.Download
    "FilterList" -> MiuixIcons.Filter
    "Lock" -> MiuixIcons.Lock
    "Fingerprint", "Gavel", "Key", "Policy", "PrivacyTip", "Security", "Shield", "VerifiedUser", "VpnKey" -> source
    "Folder" -> MiuixIcons.Folder
    "FolderOpen", "FolderZip", "Inventory2", "Storage" -> source
    "Home", "HomeOutline" -> MiuixIcons.Home
    "Info", "TipsAndUpdates" -> MiuixIcons.Info
    "KeyboardArrowRight" -> MiuixIcons.ChevronForward
    "Language" -> source
    "Map" -> MiuixIcons.MapAlbum
    "Memory", "ViewInAr" -> source
    "MenuBook" -> MiuixIcons.Notes
    "Menu", "MenuOpen" -> MiuixIcons.Sidebar
    "MoreVert" -> MiuixIcons.More
    "NewReleases" -> MiuixIcons.Promotions
    "NotificationsActive" -> MiuixIcons.Alarm
    "OpenInNew" -> MiuixIcons.Forward
    "QuestionMark" -> MiuixIcons.Help
    "Refresh" -> MiuixIcons.Refresh
    "Schedule", "Timer" -> MiuixIcons.Timer
    "Search" -> MiuixIcons.Search
    "Speed" -> MiuixIcons.Stopwatch
    "SystemUpdate", "Update" -> MiuixIcons.Update
    "Translate" -> MiuixIcons.Translate
    "Tag" -> MiuixIcons.Pin
    "Visibility" -> MiuixIcons.Show
    "VisibilityOff" -> MiuixIcons.Hide
    else -> source
}
